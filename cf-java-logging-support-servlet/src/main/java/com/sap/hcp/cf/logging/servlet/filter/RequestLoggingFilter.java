package com.sap.hcp.cf.logging.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.sap.hcp.cf.logging.common.Defaults;
import com.sap.hcp.cf.logging.common.Fields;
import com.sap.hcp.cf.logging.common.HttpHeaders;
import com.sap.hcp.cf.logging.common.LogContext;
import com.sap.hcp.cf.logging.common.LongValue;
import com.sap.hcp.cf.logging.common.Markers;
import com.sap.hcp.cf.logging.common.RequestRecord;

/**
 * A simple servlet filter that logs HTTP request processing info.
 *
 */
public class RequestLoggingFilter implements Filter {

    public static final String LOG_PROVIDER = "[SERVLET]";
    public static final String WRAP_RESPONSE_INIT_PARAM = "wrapResponse";
    public static final String WRAP_REQUEST_INIT_PARAM = "wrapRequest";

    private boolean wrapResponse = true;
    private boolean wrapRequest = true;
    private final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String value = filterConfig.getInitParameter(WRAP_RESPONSE_INIT_PARAM);
        if (value != null && "false".equalsIgnoreCase(value)) {
            wrapResponse = false;
        }
        value = filterConfig.getInitParameter(WRAP_REQUEST_INIT_PARAM);
        if (value != null && "false".equalsIgnoreCase(value)) {
            wrapRequest = false;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                              ServletException {
        if (HttpServletRequest.class.isAssignableFrom(request.getClass()) && HttpServletResponse.class.isAssignableFrom(
                                                                                                                        response.getClass())) {
            doFilterRequest((HttpServletRequest) request, (HttpServletResponse) response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        MDC.clear();
    }

    private void doFilterRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain)
                                                                                                                      throws IOException,
                                                                                                                      ServletException {

        /*
         * -- make sure correlation id is read from headers
         */
        LogContext.initializeContext(getCorrelationIdFromHeader(httpRequest));

        RequestRecord rr = null;
        try {
            rr = new RequestRecord(LOG_PROVIDER);
            ContentLengthTrackingResponseWrapper responseWrapper = null;
            ContentLengthTrackingRequestWrapper requestWrapper = null;

            /*
             * -- we essentially do three things here: -- a) we create a log
             * record using our library and log it via STDOUT -- b) keep track
             * of certain header fields so that they are available in later
             * processing steps -- b) inject a response wrapper to keep track of
             * content length (hopefully)
             */
            if (wrapResponse) {
                responseWrapper = new ContentLengthTrackingResponseWrapper(httpResponse);
            }
            if (wrapRequest) {

                requestWrapper = new ContentLengthTrackingRequestWrapper(httpRequest);
            }

            addHeaders(httpRequest, rr);

            /* -- start measuring right before calling up the filter chain -- */
            rr.start();
            if (chain != null) {
                chain.doFilter(requestWrapper != null ? requestWrapper : httpRequest, responseWrapper != null
                                                                                                              ? responseWrapper
                                                                                                              : httpResponse);
            }
            rr.stop();

            if (requestWrapper != null) {
                rr.addValue(Fields.REQUEST_SIZE_B, new LongValue(requestWrapper.getContentLength()));
            } else {
                rr.addValue(Fields.REQUEST_SIZE_B, new LongValue(httpRequest.getContentLength()));
            }
            String headerValue = httpResponse.getHeader(HttpHeaders.CONTENT_LENGTH);
            if (headerValue != null) {
                rr.addValue(Fields.RESPONSE_SIZE_B, new LongValue(Long.valueOf(headerValue)));
            } else {
                if (responseWrapper != null) {
                    rr.addValue(Fields.RESPONSE_SIZE_B, new LongValue(responseWrapper.getContentLength()));
                }
            }
            rr.addTag(Fields.RESPONSE_CONTENT_TYPE, getValue(httpResponse.getHeader(HttpHeaders.CONTENT_TYPE)));
            rr.addValue(Fields.RESPONSE_STATUS, new LongValue(httpResponse.getStatus()));
            /*
             * -- log info
             */
            logger.info(Markers.REQUEST_MARKER, rr.toString());
            /*
             * -- close this
             */
        } finally {
            rr.close();
        }
    }

    private String getCorrelationIdFromHeader(HttpServletRequest httpRequest) {
        String cId = httpRequest.getHeader(HttpHeaders.CORRELATION_ID);
        if (cId == null || cId.length() == 0) {
            cId = httpRequest.getHeader(HttpHeaders.X_VCAP_REQUEST_ID);
        }
        return cId;
    }

    private String getHeader(HttpServletRequest request, String headerName) {
        return getValue(request.getHeader(headerName));
    }

    private String getValue(String value) {
        return value != null ? value : Defaults.UNKNOWN;
    }

    private void addHeaders(HttpServletRequest request, RequestRecord lrec) {
        lrec.addTag(Fields.REQUEST, request.getQueryString() != null ? request.getRequestURI() + "?" + request
                                                                                                              .getQueryString()
                                                                     : request.getRequestURI());
        lrec.addTag(Fields.METHOD, request.getMethod());
        lrec.addTag(Fields.PROTOCOL, getValue(request.getProtocol()));
        lrec.addTag(Fields.REMOTE_IP, getValue(request.getRemoteAddr()));
        lrec.addTag(Fields.REMOTE_HOST, getValue(request.getRemoteHost()));
        lrec.addTag(Fields.REMOTE_PORT, Integer.toString(request.getRemotePort()));
        lrec.addTag(Fields.REMOTE_USER, getValue(request.getRemoteUser()));
        lrec.addTag(Fields.REFERER, getHeader(request, HttpHeaders.REFERER));
        lrec.addTag(Fields.X_FORWARDED_FOR, getHeader(request, HttpHeaders.X_FORWARDED_FOR));

        lrec.addContextTag(Fields.REQUEST_ID, getHeader(request, HttpHeaders.X_VCAP_REQUEST_ID));

    }

}

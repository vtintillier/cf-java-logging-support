package com.sap.hcp.cf.logging.servlet.filter;

import static com.sap.hcp.cf.logging.common.RequestRecordConfigurator.to;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;

import com.sap.hcp.cf.logging.common.Defaults;
import com.sap.hcp.cf.logging.common.Fields;
import com.sap.hcp.cf.logging.common.HttpHeaders;
import com.sap.hcp.cf.logging.common.LogContext;
import com.sap.hcp.cf.logging.common.LogOptionalFieldsSettings;
import com.sap.hcp.cf.logging.common.RequestRecord;
import com.sap.hcp.cf.logging.servlet.dynlog.DynLogEnvironment;
import com.sap.hcp.cf.logging.servlet.dynlog.DynamicLogLevelProcessor;

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
	private DynLogEnvironment dynLogEnvironment;
	private DynamicLogLevelProcessor dynamicLogLevelProcessor;
	protected LogOptionalFieldsSettings logOptionalFieldsSettings;

	public RequestLoggingFilter() {
		String invokingClass = this.getClass().getName().toString();
		logOptionalFieldsSettings = new LogOptionalFieldsSettings(invokingClass);
		dynLogEnvironment = new DynLogEnvironment();
		if (dynLogEnvironment.getRsaPublicKey() != null) {
			dynamicLogLevelProcessor = new DynamicLogLevelProcessor(dynLogEnvironment);
		}
	}

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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (HttpServletRequest.class.isAssignableFrom(request.getClass())
				&& HttpServletResponse.class.isAssignableFrom(response.getClass())) {
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
			throws IOException, ServletException {
		if (httpRequest.getHeader(dynLogEnvironment.getDynLogHeaderKey()) != null && dynamicLogLevelProcessor != null) {
			dynamicLogLevelProcessor.copyDynamicLogLevelToMDC(httpRequest);
		}
		/*
		 * -- make sure correlation id is read from headers
		 */
		LogContext.initializeContext(getCorrelationIdFromHeader(httpRequest));

		try {
			RequestRecord rr = new RequestRecord(LOG_PROVIDER);
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

			RequestLoggingVisitor loggingVisitor = new RequestLoggingVisitor(rr, responseWrapper);

			if (wrapRequest) {
				httpRequest = new LoggingContextRequestWrapper(httpRequest, loggingVisitor);
				httpRequest = new ContentLengthTrackingRequestWrapper(httpRequest);
			}

			addHeaders(httpRequest, rr);
			httpRequest.setAttribute(MDC.class.getName(), MDC.getCopyOfContextMap());


			/* -- start measuring right before calling up the filter chain -- */
			rr.start();
			if (chain != null) {
				chain.doFilter(httpRequest, httpResponse);
			}

			if (!httpRequest.isAsyncStarted()) {
				loggingVisitor.logRequest(httpRequest, httpResponse);
			}
			/*
			 * -- close this
			 */
		} finally {
			if (dynamicLogLevelProcessor != null) {
				dynamicLogLevelProcessor.removeDynamicLogLevelFromMDC();
			}
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
		lrec.addTag(Fields.REQUEST, request.getQueryString() != null
				? request.getRequestURI() + "?" + request.getQueryString() : request.getRequestURI());
		lrec.addTag(Fields.METHOD, request.getMethod());
		lrec.addTag(Fields.PROTOCOL, getValue(request.getProtocol()));

		boolean isSensitiveConnectionData = logOptionalFieldsSettings.isLogSensitiveConnectionData();

		to(lrec).addOptionalTag(isSensitiveConnectionData, Fields.REMOTE_IP, getValue(request.getRemoteAddr()))
				.addOptionalTag(isSensitiveConnectionData, Fields.REMOTE_HOST, getValue(request.getRemoteHost()))
				.addOptionalTag(isSensitiveConnectionData, Fields.REMOTE_PORT,
						Integer.toString(request.getRemotePort()))
				.addOptionalTag(isSensitiveConnectionData, Fields.X_FORWARDED_FOR,
						getHeader(request, HttpHeaders.X_FORWARDED_FOR))
				.addOptionalTag(logOptionalFieldsSettings.isLogRemoteUserField(), Fields.REMOTE_USER,
						getValue(request.getRemoteUser()))
				.addOptionalTag(logOptionalFieldsSettings.isLogRefererField(), Fields.REFERER,
						getHeader(request, HttpHeaders.REFERER));
		lrec.addContextTag(Fields.REQUEST_ID, getHeader(request, HttpHeaders.X_VCAP_REQUEST_ID));
	}
}

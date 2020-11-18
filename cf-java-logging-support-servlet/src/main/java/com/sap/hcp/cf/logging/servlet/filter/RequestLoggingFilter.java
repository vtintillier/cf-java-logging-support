package com.sap.hcp.cf.logging.servlet.filter;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.ConcurrentInitializer;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.sap.hcp.cf.logging.common.LogContext;
import com.sap.hcp.cf.logging.common.LogOptionalFieldsSettings;
import com.sap.hcp.cf.logging.common.request.HttpHeader;
import com.sap.hcp.cf.logging.common.request.HttpHeaders;
import com.sap.hcp.cf.logging.common.request.RequestRecord;
import com.sap.hcp.cf.logging.servlet.dynlog.DynLogConfiguration;
import com.sap.hcp.cf.logging.servlet.dynlog.DynLogEnvironment;
import com.sap.hcp.cf.logging.servlet.dynlog.DynamicLogLevelProcessor;

/**
 * <p>
 * A simple servlet filter that logs HTTP request processing info. It will read
 * several HTTP Headers and store them in the SLF4J MDC, so that all log
 * messages created during request handling will have those additional fields.
 * It will also instrument the request to generate a request log containing
 * metrics such as request and response sizes and response time. This
 * instrumentation can be disabled by denying logs from {@link RequestLogger}
 * with marker "request".
 * </p>
 * <p>
 * This filter will generate a correlation id, from the HTTP header
 * "X-CorrelationID" falling back to "x-vcap-request-id" if not found or using a
 * random UUID. The correlation id will be added as an HTTP header
 * "X-CorrelationID" to the response if possible.
 * </p>
 * <p>
 * This filter supports dynamic log levels activated by JWT tokens in HTTP
 * headers. Setup and processing of these tokens can be changed with own
 * implementations of {@link DynLogEnvironment} and
 * {@link DynamicLogLevelProcessor}. For integration provide a subclass of
 * {@link RequestLoggingFilter} and overwrite
 * {@link RequestLoggingFilter#getDynLogConfiguration()} and
 * {@link RequestLoggingFilter#getDynLogLevelProcessor()}.
 * </p>
 * <p>
 * To use the filter, it needs to be added to the servlet configuration. It has
 * a default constructor to support web.xml configuration. There are several
 * other constructors that support some customization, in case dynamic
 * configuration (e.g. Spring Boot) is used. You can add further customization
 * by subclassing the filter and overwrite its methods.
 * </p>
 */
public class RequestLoggingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingFilter.class);

    public static final String LOG_PROVIDER = "[SERVLET]";
    public static final String WRAP_RESPONSE_INIT_PARAM = "wrapResponse";
    public static final String WRAP_REQUEST_INIT_PARAM = "wrapRequest";

    private boolean wrapResponse = true;
    private boolean wrapRequest = true;
    private ConcurrentInitializer<DynLogConfiguration> dynLogEnvironment;
    private ConcurrentInitializer<DynamicLogLevelProcessor> dynamicLogLevelProcessor;
    private RequestRecordFactory requestRecordFactory;

    public RequestLoggingFilter() {
        this(createDefaultRequestRecordFactory());
    }

    private static RequestRecordFactory createDefaultRequestRecordFactory() {
        String invokingClass = RequestLoggingFilter.class.getName();
        LogOptionalFieldsSettings logOptionalFieldsSettings = new LogOptionalFieldsSettings(invokingClass);
        return new RequestRecordFactory(logOptionalFieldsSettings);
    }

    public RequestLoggingFilter(RequestRecordFactory requestRecordFactory) {
        this(requestRecordFactory, createDefaultDynLogEnvironment());
    }

    private static ConcurrentInitializer<DynLogConfiguration> createDefaultDynLogEnvironment() {
        DynLogEnvironment environment = new DynLogEnvironment();
        return () -> environment;
    }

    public RequestLoggingFilter(ConcurrentInitializer<DynLogConfiguration> dynLogEnvironment) {
        this(createDefaultRequestRecordFactory(), dynLogEnvironment);
    }
    
    public RequestLoggingFilter(RequestRecordFactory requestRecordFactory,
                                ConcurrentInitializer<DynLogConfiguration> dynLogEnvironment) {
        this.requestRecordFactory = requestRecordFactory;
        this.dynLogEnvironment = dynLogEnvironment;
        this.dynamicLogLevelProcessor = new LazyInitializer<DynamicLogLevelProcessor>() {

            @Override
            protected DynamicLogLevelProcessor initialize() throws ConcurrentException {
                return getDynLogConfiguration().map(DynLogConfiguration::getRsaPublicKey).map(DynamicLogLevelProcessor::new)
                                             .get();
            }
        };
    }

    public RequestLoggingFilter(ConcurrentInitializer<DynLogConfiguration> dynLogEnvironment,
                                ConcurrentInitializer<DynamicLogLevelProcessor> dynamicLogLevelProcessor) {
        this(createDefaultRequestRecordFactory(), dynLogEnvironment, dynamicLogLevelProcessor);
    }

    public RequestLoggingFilter(RequestRecordFactory requestRecordFactory,
                                ConcurrentInitializer<DynLogConfiguration> dynLogEnvironment,
                                ConcurrentInitializer<DynamicLogLevelProcessor> dynamicLogLevelProcessor) {
        this.requestRecordFactory = requestRecordFactory;
        this.dynLogEnvironment = dynLogEnvironment;
        this.dynamicLogLevelProcessor = dynamicLogLevelProcessor;
    }

    protected Optional<DynLogConfiguration> getDynLogConfiguration() {
        try {
            return Optional.of(dynLogEnvironment.get());
        } catch (ConcurrentException cause) {
            LOG.debug("Cannot initialize dynamic log level environment. Will continue without this feature", cause);
            return Optional.empty();
        }
    }

    protected Optional<DynamicLogLevelProcessor> getDynLogLevelProcessor() {
        try {
            if (getDynLogConfiguration().map(DynLogConfiguration::getRsaPublicKey).isPresent()) {
                return Optional.of(dynamicLogLevelProcessor.get());
            }
        } catch (ConcurrentException cause) {
            LOG.debug("Cannot initialize dynamic log level processor. Will continue without this feature", cause);
        }
        return Optional.empty();
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
        activateDynamicLogLevels(httpRequest);
        /*
         * -- make sure correlation id is read from headers
         */
        LogContext.initializeContext(HttpHeaderUtilities.getHeaderValue(httpRequest, HttpHeaders.CORRELATION_ID));

        try {

            RequestRecord rr = requestRecordFactory.create(httpRequest);
            httpRequest.setAttribute(MDC.class.getName(), MDC.getCopyOfContextMap());

            if (!httpResponse.isCommitted() && httpResponse.getHeader(HttpHeaders.CORRELATION_ID.getName()) == null) {
                httpResponse.setHeader(HttpHeaders.CORRELATION_ID.getName(), LogContext.getCorrelationId());
            }

            /*
             * If request logging is disabled skip request instrumentation and
             * continue the filter chain immediately.
             */
            if (!RequestLogger.isRequestLoggingEnabled()) {
                doFilter(chain, httpRequest, httpResponse);
                return;
            }

            /*
             * -- we essentially do three things here: -- a) we create a log
             * record using our library and log it via STDOUT -- b) keep track
             * of certain header fields so that they are available in later
             * processing steps -- b) inject a response wrapper to keep track of
             * content length (hopefully)
             */
            if (wrapResponse) {
                httpResponse = new ContentLengthTrackingResponseWrapper(httpResponse);
            }

            if (wrapRequest) {
                httpRequest = new ContentLengthTrackingRequestWrapper(httpRequest);
            }

            RequestLogger loggingVisitor = new RequestLogger(rr, httpRequest, httpResponse);
            httpRequest = new LoggingContextRequestWrapper(httpRequest, loggingVisitor);

            /* -- start measuring right before calling up the filter chain -- */
            rr.start();
            doFilter(chain, httpRequest, httpResponse);

            if (!httpRequest.isAsyncStarted()) {
                loggingVisitor.logRequest();
            }
            /*
             * -- close this
             */
        } finally {
            deactivateDynamicLogLevels();
            resetLogContext();
        }
    }

    private void activateDynamicLogLevels(HttpServletRequest httpRequest) {
        getDynLogLevelProcessor().ifPresent(processor -> {
            getDynLogConfiguration().map(env -> env.getDynLogHeaderValue(httpRequest)).ifPresent(
                                                                                               processor::copyDynamicLogLevelToMDC);
        });
    }

    private void deactivateDynamicLogLevels() {
        getDynLogLevelProcessor().ifPresent(DynamicLogLevelProcessor::removeDynamicLogLevelFromMDC);
    }

    private void resetLogContext() {
        for (HttpHeader header: HttpHeaders.propagated()) {
            LogContext.remove(header.getField());
        }
        LogContext.resetContextFields();
    }

    private void doFilter(FilterChain chain, HttpServletRequest httpRequest, HttpServletResponse httpResponse)
                                                                                                               throws IOException,
                                                                                                               ServletException {
        if (chain != null) {
            chain.doFilter(httpRequest, httpResponse);
        }
    }

}

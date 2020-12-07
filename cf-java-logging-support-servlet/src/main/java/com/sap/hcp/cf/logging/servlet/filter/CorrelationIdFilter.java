package com.sap.hcp.cf.logging.servlet.filter;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hcp.cf.logging.common.Defaults;
import com.sap.hcp.cf.logging.common.LogContext;
import com.sap.hcp.cf.logging.common.request.HttpHeader;
import com.sap.hcp.cf.logging.common.request.HttpHeaders;

/**
 * The {@link CorrelationIdFilter} extracts a correlation id according to
 * {@link HttpHeaders#CORRELATION_ID}. It will generate a random uuid, if no
 * correlation id is found in the headers. In any case the correlation id is set
 * as a response header, if possible
 */
public class CorrelationIdFilter extends AbstractLoggingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CorrelationIdFilter.class);
    private HttpHeader correlationHeader;

    public CorrelationIdFilter() {
        this(HttpHeaders.CORRELATION_ID);
    }
    
    public CorrelationIdFilter(HttpHeader correlationHeader) {
        this.correlationHeader = correlationHeader;
    }

    @Override
    protected void preProcess(HttpServletRequest request, HttpServletResponse response) {
        String correlationId = determineCorrelationId(request);
        LogContext.add(correlationHeader.getField(), correlationId);
        addCorrelationIdHeader(response, correlationId);
    }

    private String determineCorrelationId(HttpServletRequest request) {
        String correlationId = HttpHeaderUtilities.getHeaderValue(request, correlationHeader);
        if (correlationId == null || correlationId.isEmpty() || correlationId.equals(Defaults.UNKNOWN)) {
            correlationId = String.valueOf(UUID.randomUUID());
            LOG.debug("Generated new correlation-id <{}>", correlationId);
        }
        return correlationId;
    }

    private void addCorrelationIdHeader(HttpServletResponse response, String correlationId) {
        if (!response.isCommitted() && response.getHeader(correlationHeader.getName()) == null) {
            response.setHeader(correlationHeader.getName(), correlationId);
        }
    }
    
    @Override
    protected void postProcess(HttpServletRequest request, HttpServletResponse response) {
        LogContext.remove(correlationHeader.getField());
    }

}

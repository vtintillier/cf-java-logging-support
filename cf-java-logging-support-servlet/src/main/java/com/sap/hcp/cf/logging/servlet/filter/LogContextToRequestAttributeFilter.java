package com.sap.hcp.cf.logging.servlet.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;

/**
 * The {@link LogContextToRequestAttributeFilter} adds the current {@link MDC}
 * as a RequestAttribute to the current request. This helps with asynchronous
 * reqquest handling, where the MDC is not propagated correctly across thread
 * boundaries. The correct LogContext can be restored by
 * {@code MDC.setContextMap((Map<String, String>) httpRequest.getAttribute(MDC.class.getName()))}
 */
public class LogContextToRequestAttributeFilter extends AbstractLoggingFilter {

    @Override
    protected void preProcess(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute(MDC.class.getName(), MDC.getCopyOfContextMap());
    }

}

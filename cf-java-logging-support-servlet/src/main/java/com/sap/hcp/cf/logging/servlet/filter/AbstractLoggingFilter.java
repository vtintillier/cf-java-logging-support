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

public abstract class AbstractLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                              ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            doFilterRequest((HttpServletRequest) request, (HttpServletResponse) response, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * Provides a default implementation for handling servlet requests already
     * cast to {@link HttpServletRequest} and {@link HttpServletResponse}.
     * Custom implementations of {@link AbstractLoggingFilter} should overwrite
     * {@link #preProcess(HttpServletRequest, HttpServletResponse)} and/or
     * {@link #postProcess(HttpServletRequest, HttpServletResponse)}.
     * 
     * @param request
     *            cast as {@link HttpServletRequest}
     * @param response
     *            cast as {@link HttpServletResponse}
     * @param chain
     *            the {@link FilterChain} to continue request handling
     * @throws IOException
     * @throws ServletException
     */
    protected void doFilterRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
                                                                                                                throws IOException,
                                                                                                                ServletException {
        try {
            preProcess(request, response);
            chain.doFilter(request, response);
        } finally {
            postProcess(request, response);

        }
    }

    /**
     * Pre-process the request/response before it is passed along the filter
     * chain.
     * 
     * @param request
     * @param response
     */
    protected void preProcess(HttpServletRequest request, HttpServletResponse response) {
    }

    /**
     * Post-process the request/response after it was handled by the filter
     * chain. This is executed even in cases of failures during handling. Use
     * this method to reset or clean-up state, e.g. MDC.
     * 
     * @param request
     * @param response
     */
    protected void postProcess(HttpServletRequest request, HttpServletResponse response) {
    }

    /**
     * This is an empty implementation for filters, that do not require
     * initialization.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // nothing to do
    }

    /**
     * this is an empty implementation for tolters, that do not require clean-up
     * of state.
     */
    @Override
    public void destroy() {
        // nothing to do
    }
}

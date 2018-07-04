package com.sap.hcp.cf.logging.servlet.filter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class LoggingContextRequestWrapper extends HttpServletRequestWrapper {

	private RequestLoggingVisitor loggingVisitor;

	public LoggingContextRequestWrapper(HttpServletRequest request, RequestLoggingVisitor loggingVisitor) {
		super(request);
		this.loggingVisitor = loggingVisitor;
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		return new LoggingAsyncContextImpl(super.startAsync(), loggingVisitor);
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
			throws IllegalStateException {
		return new LoggingAsyncContextImpl(super.startAsync(servletRequest, servletResponse), loggingVisitor);
	}
}

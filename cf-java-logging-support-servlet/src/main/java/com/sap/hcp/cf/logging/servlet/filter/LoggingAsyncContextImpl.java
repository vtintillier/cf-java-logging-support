package com.sap.hcp.cf.logging.servlet.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;

public class LoggingAsyncContextImpl implements AsyncContext {

	private AsyncContext asyncContext;

	public LoggingAsyncContextImpl(AsyncContext asyncContext, final RequestLoggingVisitor loggingVisitor) {
		this.asyncContext = asyncContext;
		asyncContext.addListener(new AsyncListener() {

			@Override
			public void onTimeout(AsyncEvent event) throws IOException {
				generateLog(loggingVisitor);
			}

			private void generateLog(final RequestLoggingVisitor loggingVisitor) {
				ServletRequest request = getRequest();
				ServletResponse response = getResponse();
				if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
					HttpServletRequest httpRequest = (HttpServletRequest) request;
					HttpServletResponse httpResponse = (HttpServletResponse) response;
					Map<String, String> contextMap = getContextMap();
					Map<String, String> currentContextMap = MDC.getCopyOfContextMap();
					try {
						MDC.setContextMap(contextMap);
						loggingVisitor.logRequest(httpRequest, httpResponse);
					} finally {
						if (currentContextMap != null) {
							MDC.setContextMap(currentContextMap);
						}
					}
				}
			}


			@Override
			public void onStartAsync(AsyncEvent event) throws IOException {
			}

			@Override
			public void onError(AsyncEvent event) throws IOException {
				generateLog(loggingVisitor);
			}

			@Override
			public void onComplete(AsyncEvent event) throws IOException {
				generateLog(loggingVisitor);
			}
		});
	}

	private Map<String, String> getContextMap() {
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> fromRequest = (Map<String, String>) getRequest().getAttribute(MDC.class.getName());
			return fromRequest != null ? fromRequest : Collections.emptyMap();
		} catch (ClassCastException ignored) {
			return Collections.emptyMap();
		}
	}

	@Override
	public void start(Runnable run) {
		try {
			Map<String, String> contextMap = getContextMap();
			asyncContext.start(new Runnable() {

				@Override
				public void run() {
					Map<String, String> currentContextMap = MDC.getCopyOfContextMap();
					try {
						MDC.setContextMap(contextMap);
						run.run();
					} finally {
						if (currentContextMap != null) {
							MDC.setContextMap(currentContextMap);
						}
					}
				}
			});
		} catch (ClassCastException ignored) {
			asyncContext.start(run);
		}
	}

	@Override
	public ServletRequest getRequest() {
		return asyncContext.getRequest();
	}

	@Override
	public ServletResponse getResponse() {
		return asyncContext.getResponse();
	}

	@Override
	public boolean hasOriginalRequestAndResponse() {
		return asyncContext.hasOriginalRequestAndResponse();
	}

	@Override
	public void dispatch() {
		asyncContext.dispatch();
	}

	@Override
	public void dispatch(String path) {
		asyncContext.dispatch(path);
	}

	@Override
	public void dispatch(ServletContext context, String path) {
		asyncContext.dispatch(context, path);
	}

	@Override
	public void complete() {
		asyncContext.complete();
	}

	@Override
	public void addListener(AsyncListener listener) {
		asyncContext.addListener(listener);
	}

	@Override
	public void addListener(AsyncListener listener, ServletRequest servletRequest, ServletResponse servletResponse) {
		asyncContext.addListener(listener, servletRequest, servletResponse);
	}

	@Override
	public <T extends AsyncListener> T createListener(Class<T> clazz) throws ServletException {
		return asyncContext.createListener(clazz);
	}

	@Override
	public void setTimeout(long timeout) {
		asyncContext.setTimeout(timeout);
	}

	@Override
	public long getTimeout() {
		return asyncContext.getTimeout();
	}

}

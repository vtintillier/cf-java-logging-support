package com.sap.hcp.cf.logging.servlet.filter;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.sap.hcp.cf.logging.common.Defaults;
import com.sap.hcp.cf.logging.common.Fields;
import com.sap.hcp.cf.logging.common.HttpHeaders;
import com.sap.hcp.cf.logging.common.LongValue;
import com.sap.hcp.cf.logging.common.Markers;
import com.sap.hcp.cf.logging.common.RequestRecord;

public class RequestLogger {

	private static final Logger LOG = LoggerFactory.getLogger(RequestLogger.class);

	private HttpServletRequest httpRequest;
	private HttpServletResponse httpResponse;
	private RequestRecord requestRecord;

	public RequestLogger(RequestRecord requestRecord, HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) {
		this.requestRecord = requestRecord;
		this.httpRequest = httpRequest;
		this.httpResponse = httpResponse;
	}

	public void logRequest() {
		requestRecord.stop();
		addRequestHandlingParameters();
		generateLog();
	}

	private void addRequestHandlingParameters() {
		requestRecord.addValue(Fields.REQUEST_SIZE_B, new LongValue(httpRequest.getContentLength()));
		LongValue responseSize = getResponseSize(httpResponse);
		if (responseSize != null) {
			requestRecord.addValue(Fields.RESPONSE_SIZE_B, responseSize);
		}
		requestRecord.addTag(Fields.RESPONSE_CONTENT_TYPE, getValue(httpResponse.getHeader(HttpHeaders.CONTENT_TYPE)));
		requestRecord.addValue(Fields.RESPONSE_STATUS, new LongValue(httpResponse.getStatus()));
	}

	private LongValue getResponseSize(HttpServletResponse httpResponse) {
		String headerValue = httpResponse.getHeader(HttpHeaders.CONTENT_LENGTH);
		if (headerValue != null) {
			return new LongValue(Long.valueOf(headerValue));
		}
		if (httpResponse != null && httpResponse instanceof ContentLengthTrackingResponseWrapper) {
			ContentLengthTrackingResponseWrapper wrapper = (ContentLengthTrackingResponseWrapper) httpResponse;
			return new LongValue(wrapper.getContentLength());
		}
		return null;
	}

	private String getValue(String value) {
		return value != null ? value : Defaults.UNKNOWN;
	}

	private void generateLog() {
		Map<String, String> contextMap = getContextMap();
		Map<String, String> currentContextMap = MDC.getCopyOfContextMap();
		try {
			MDC.setContextMap(contextMap);
			LOG.info(Markers.REQUEST_MARKER, "{}", requestRecord);
		} finally {
			if (currentContextMap != null) {
				MDC.setContextMap(currentContextMap);
			}
		}
	}

	private Map<String, String> getContextMap() {
		try {
			@SuppressWarnings("unchecked")
			Map<String, String> fromRequest = (Map<String, String>) httpRequest.getAttribute(MDC.class.getName());
			return fromRequest != null ? fromRequest : Collections.emptyMap();
		} catch (ClassCastException ignored) {
			return Collections.emptyMap();
		}
	}

}

package com.sap.hcp.cf.logging.servlet.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hcp.cf.logging.common.Defaults;
import com.sap.hcp.cf.logging.common.Fields;
import com.sap.hcp.cf.logging.common.HttpHeaders;
import com.sap.hcp.cf.logging.common.LongValue;
import com.sap.hcp.cf.logging.common.Markers;
import com.sap.hcp.cf.logging.common.RequestRecord;

public class RequestLoggingVisitor {

	private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingVisitor.class);

	private ContentLengthTrackingResponseWrapper responseWrapper;
	private RequestRecord requestRecord;

	public RequestLoggingVisitor(RequestRecord requestRecord, ContentLengthTrackingResponseWrapper responseWrapper) {
		this.requestRecord = requestRecord;
		this.responseWrapper = responseWrapper;
	}

	public void logRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		requestRecord.stop();
		requestRecord.addValue(Fields.REQUEST_SIZE_B, new LongValue(httpRequest.getContentLength()));
		String headerValue = httpResponse.getHeader(HttpHeaders.CONTENT_LENGTH);
		if (headerValue != null) {
			requestRecord.addValue(Fields.RESPONSE_SIZE_B, new LongValue(Long.valueOf(headerValue)));
		} else {
			if (responseWrapper != null) {
				requestRecord.addValue(Fields.RESPONSE_SIZE_B, new LongValue(responseWrapper.getContentLength()));
			}
		}
		requestRecord.addTag(Fields.RESPONSE_CONTENT_TYPE, getValue(httpResponse.getHeader(HttpHeaders.CONTENT_TYPE)));
		requestRecord.addValue(Fields.RESPONSE_STATUS, new LongValue(httpResponse.getStatus()));

		LOG.info(Markers.REQUEST_MARKER, "{}", requestRecord);

	}

	private String getValue(String value) {
		return value != null ? value : Defaults.UNKNOWN;
	}

}

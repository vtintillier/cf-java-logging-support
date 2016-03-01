package com.sap.hcp.cf.logging.jersey.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hcp.cf.logging.common.Fields;
import com.sap.hcp.cf.logging.common.HttpHeaders;
import com.sap.hcp.cf.logging.common.LongValue;
import com.sap.hcp.cf.logging.common.Markers;
import com.sap.hcp.cf.logging.common.RequestRecord;

public class ResponseHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseHandler.class);
	
	public void handle(ResponseContextAdapter responseContext, RequestRecord logRecord) {
		if (logRecord != null) {
			logRecord.addValue(Fields.RESPONSE_SIZE_B, new LongValue(responseContext.getLength()));
			logRecord.addTag(Fields.RESPONSE_CONTENT_TYPE, responseContext.getHeader(HttpHeaders.CONTENT_TYPE));   
			logRecord.addValue(Fields.RESPONSE_STATUS, new LongValue(responseContext.getStatus()));
			logRecord.stop();
			LOGGER.info(Markers.REQUEST_MARKER, logRecord.toString());
			logRecord.resetContext();
		}
	}
}

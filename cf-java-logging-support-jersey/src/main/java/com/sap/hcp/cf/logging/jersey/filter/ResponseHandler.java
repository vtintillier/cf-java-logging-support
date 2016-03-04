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
	
	public void handle(ResponseContextAdapter responseContext, RequestRecord rr) {
		if (rr != null) {
			rr.addValue(Fields.RESPONSE_SIZE_B, new LongValue(responseContext.getLength()));
			rr.addTag(Fields.RESPONSE_CONTENT_TYPE, responseContext.getHeader(HttpHeaders.CONTENT_TYPE));   
			rr.addValue(Fields.RESPONSE_STATUS, new LongValue(responseContext.getStatus()));
			rr.stop();
			LOGGER.info(Markers.REQUEST_MARKER, rr.toString());
			/*
			 * -- close this instance
			 */
			rr.close();
		}
		else {
			LOGGER.error("No record found to handle response {}", responseContext);
		}
	}
}

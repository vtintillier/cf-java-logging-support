package com.sap.hcp.cf.logging.jersey.filter;

import org.slf4j.LoggerFactory;

import com.sap.hcp.cf.logging.common.RequestRecord;

import static com.sap.hcp.cf.logging.jersey.filter.Utils.REQ_RECORD_KEY;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class PerfXClientResponseFilter implements ClientResponseFilter {

	private final ResponseHandler handler;
	
	public PerfXClientResponseFilter() {
		this.handler = new ResponseHandler();
	}
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		try {
			handler.handle(new ClientResponseContextAdapter(responseContext), (RequestRecord) requestContext.getProperty(REQ_RECORD_KEY));
		}
		catch (Exception ex) {
			LoggerFactory.getLogger(PerfXClientResponseFilter.class).error("Can't handle client response", ex);
		}
	}

}

package com.sap.hcp.cf.logging.jersey.filter;

import org.slf4j.LoggerFactory;

import com.sap.hcp.cf.logging.common.RequestRecord;

import static com.sap.hcp.cf.logging.jersey.filter.Utils.REQ_RECORD_KEY;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class PerfXContainerResponseFilter implements ContainerResponseFilter {

	private final ResponseHandler handler;

	public PerfXContainerResponseFilter() {
		this.handler = new ResponseHandler();
	}
	
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		try {
			handler.handle(new ContainerResponseContextAdapter(responseContext), (RequestRecord) requestContext.getProperty(REQ_RECORD_KEY));
		}
		catch (Exception ex) {
			LoggerFactory.getLogger(PerfXContainerResponseFilter.class).error("Cannot handle container request", ex);
		}
	}

}

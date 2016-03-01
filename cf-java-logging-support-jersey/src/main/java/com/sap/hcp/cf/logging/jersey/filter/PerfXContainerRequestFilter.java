package com.sap.hcp.cf.logging.jersey.filter;

import static com.sap.hcp.cf.logging.jersey.filter.Utils.REQ_RECORD_KEY;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class PerfXContainerRequestFilter implements ContainerRequestFilter {

	private final RequestHandler handler;
	
	public PerfXContainerRequestFilter() {
		this.handler =  new RequestHandler();
	}
	public void filter(ContainerRequestContext requestContext) throws IOException {
		requestContext.setProperty(REQ_RECORD_KEY,  handler.handle(new ContainerRequestContextAdapter(requestContext)));
	}

}

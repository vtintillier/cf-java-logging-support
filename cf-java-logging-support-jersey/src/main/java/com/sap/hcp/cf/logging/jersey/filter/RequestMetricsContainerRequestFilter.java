package com.sap.hcp.cf.logging.jersey.filter;

import static com.sap.hcp.cf.logging.jersey.filter.Utils.REQ_METRICS_KEY;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class RequestMetricsContainerRequestFilter implements ContainerRequestFilter {

    private final RequestHandler handler;

    public RequestMetricsContainerRequestFilter() {
        handler = new RequestHandler();
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.setProperty(REQ_METRICS_KEY, handler.handle(new ContainerRequestContextAdapter(requestContext)));
    }

}

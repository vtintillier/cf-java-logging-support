package com.sap.hcp.cf.logging.jersey.filter;

import static com.sap.hcp.cf.logging.jersey.filter.Utils.REQ_METRICS_KEY;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class RequestMetricsClientRequestFilter implements ClientRequestFilter {

    private final RequestHandler handler;

    public RequestMetricsClientRequestFilter() {
        handler = new RequestHandler();
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        requestContext.setProperty(REQ_METRICS_KEY, handler.handle(new ClientRequestContextAdapter(requestContext)));
    }

}

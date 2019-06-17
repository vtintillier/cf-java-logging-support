package com.sap.hcp.cf.logging.jersey.filter;

import static com.sap.hcp.cf.logging.jersey.filter.Utils.REQ_METRICS_KEY;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.LoggerFactory;

import com.sap.hcp.cf.logging.common.request.RequestRecord;

@Provider
public class RequestMetricsContainerResponseFilter implements ContainerResponseFilter {

    private final ResponseHandler handler;

    public RequestMetricsContainerResponseFilter() {
        handler = new ResponseHandler();
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
                                                                                                         throws IOException {
        try {
            handler.handle(new ContainerResponseContextAdapter(responseContext), (RequestRecord) requestContext
                                                                                                               .getProperty(REQ_METRICS_KEY));
        } catch (Exception ex) {
            LoggerFactory.getLogger(RequestMetricsContainerResponseFilter.class).error(
                                                                                       "Cannot handle container request",
                                                                                       ex);
        }
    }

}

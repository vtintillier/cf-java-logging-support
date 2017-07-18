package com.sap.hcp.cf.logging.jersey.filter;

import javax.ws.rs.core.Configurable;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;

public class RequestMetricsFilterRegistry {

    public static void registerContainerFilters(Configurable<ResourceConfig> config) {
        config.register(RequestMetricsContainerRequestFilter.class);
        config.register(RequestMetricsContainerResponseFilter.class);
    }

    public static void registerClientFilters(ClientConfig clientConfig) {
        clientConfig.register(RequestMetricsClientRequestFilter.class);
        clientConfig.register(RequestMetricsClientResponseFilter.class);
    }
}

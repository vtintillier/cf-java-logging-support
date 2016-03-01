package com.sap.hcp.cf.logging.jersey.filter;

import javax.ws.rs.core.Configurable;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;


public class PerfXFilterRegistry {

	public static void registerContainerFilters(Configurable<ResourceConfig> config) {
		config.register(PerfXContainerRequestFilter.class);
		config.register(PerfXContainerResponseFilter.class);		
	}
	
	public static void registerClientFilters(ClientConfig clientConfig) {
		clientConfig.register(PerfXClientRequestFilter.class);
		clientConfig.register(PerfXClientResponseFilter.class);		 
	}
}

package com.sap.hcp.cf.logging.jersey.filter;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.client.ClientConfig;

/**
 * Simple Test Resource 
 *
 */

@Path("/testchainedresource")
public class TestChainedResource {

	@GET
	public Response  getHello(@Context UriInfo ui) {
		
		ClientConfig cfg = new ClientConfig();
		cfg.register(RequestMetricsClientRequestFilter.class);
		cfg.register(RequestMetricsClientResponseFilter.class);
		
		WebTarget wt = ClientBuilder.newClient(cfg).target(ui.getBaseUri() + "testresource");

		return wt.request().get();
//		return Response.status(200).build();

		       
	}
	
}
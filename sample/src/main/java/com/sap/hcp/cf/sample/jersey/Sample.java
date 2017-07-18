package com.sap.hcp.cf.sample.jersey;

import java.net.URI;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.LoggerFactory;

import com.sap.hcp.cf.logging.jersey.filter.ClientRequestUtils;
import com.sap.hcp.cf.logging.jersey.filter.RequestMetricsClientRequestFilter;
import com.sap.hcp.cf.logging.jersey.filter.RequestMetricsClientResponseFilter;

@Path("/")
public class Sample {

    private final ClientConfig clientConfig = new ClientConfig().register(RequestMetricsClientRequestFilter.class)
                                                                .register(RequestMetricsClientResponseFilter.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response greet(@DefaultValue("World") @QueryParam("greeting") String msg) {
        return Response.status(200).entity("Hello from Jersey: " + (msg != null ? msg : "") + "\n").build();
    }

    @GET
    @Path("/forward")
    public Response forward(@Context UriInfo uriInfo, @QueryParam("q") String queryParam,
                            @Context HttpHeaders headers) {
        LoggerFactory.getLogger(Sample.class).info("forwarding request");
        URI baseUri = uriInfo.getBaseUri();
        StringBuilder targetUri = new StringBuilder(baseUri.toString().replace("/jersey", ""));
        if (queryParam != null) {
            targetUri.append("?").append(queryParam);
        }
        Invocation.Builder forwReq = ClientBuilder.newClient(clientConfig).target(targetUri.toString()).request();
        forwReq = ClientRequestUtils.propagate(forwReq, headers);
        return Response.status(200).entity(forwReq.get(String.class)).build();
    }
}

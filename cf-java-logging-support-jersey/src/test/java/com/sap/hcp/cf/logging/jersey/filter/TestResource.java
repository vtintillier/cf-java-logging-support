package com.sap.hcp.cf.logging.jersey.filter;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Simple Test Resource
 *
 * @author d048888
 *
 */

@Path("/testresource")
@Produces("text/plain")
public class TestResource {
    public static final String EXPECTED_MESSAGE = "test";
    public static final double EXPECTED_REQUEST_TIME = 1.0;
    public static final int EXPECTED_STATUS_CODE = 200;
    public static final String EXPECTED_CONTENT_TYPE = "text/plain";
    public static final String EXPECTED_REQUEST_METHOD = "GET";

    @GET
    @Produces("text/plain")
    public Response getHello() {

        Response response = Response.status(EXPECTED_STATUS_CODE).entity(EXPECTED_MESSAGE).build();
        return response;

    }

    @DELETE
    @Produces("text/plain")
    public Response deleteAndWait() {
        try {
            Thread.sleep((long) EXPECTED_REQUEST_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Response response = Response.status(EXPECTED_STATUS_CODE).entity(EXPECTED_MESSAGE).build();
        return response;

    }
}

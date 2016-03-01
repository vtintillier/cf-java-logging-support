package com.sap.hcp.cf.logging.jersey.filter;

import java.net.URI;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.core.MultivaluedMap;

import com.sap.hcp.cf.logging.common.RequestRecord.Direction;

public class ClientRequestContextAdapter implements RequestContextAdapter {

	public static final String LAYER_NAME = "[JERSEY.CLIENT]";
	
	private final ClientRequestContext ctx;

	public ClientRequestContextAdapter(ClientRequestContext requestContext) {
		this.ctx = requestContext;
	}

	public String getHeader(String headerName) {
		return ctx.getHeaderString(headerName);
	}

	public String getMethod() {
		return ctx.getMethod();
	}

	public URI getUri() {
		return ctx.getUri();
	}

	public String getName() {
		return LAYER_NAME;
	}

	public Direction getDirection() {
		return Direction.OUT;
	}

	public void setHeader(String headerName, String headerValue) {
		if (headerName != null && headerValue != null) {
			MultivaluedMap<String, String > headers = ctx.getStringHeaders();
			headers.add(headerName, headerValue);
		}
	}

	public String getUser() {
		return null;
	}

	public long getRequestSize() {
		return -1;
	}
}

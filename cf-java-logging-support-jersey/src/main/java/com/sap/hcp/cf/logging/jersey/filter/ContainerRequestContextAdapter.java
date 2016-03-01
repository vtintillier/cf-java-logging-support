package com.sap.hcp.cf.logging.jersey.filter;

import java.net.URI;
import java.security.Principal;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.SecurityContext;

import com.sap.hcp.cf.logging.common.RequestRecord.Direction;

public class ContainerRequestContextAdapter implements RequestContextAdapter {

	public static final String LAYER_NAME = "[JERSEY.CONTAINER]";
	
	private final ContainerRequestContext ctx;

	public ContainerRequestContextAdapter(ContainerRequestContext requestContext) {
		this.ctx = requestContext;
	}
	
	public String getHeader(String headerName) {
		return ctx.getHeaderString(headerName);
	}

	public String getMethod() {
		return ctx.getMethod();
	}

	public URI getUri() {
		return ctx.getUriInfo().getRequestUri();
	}

	public String getName() {
		return LAYER_NAME;
	}

	public Direction getDirection() {
		return Direction.IN;
	}

	public void setHeader(String headerName, String headerValue) {
	}

	public String getUser() {
		SecurityContext sc = ctx.getSecurityContext();
		if (sc != null) {
			Principal p = sc.getUserPrincipal();
			if (p != null) {
				return p.getName();
			}
		}
		return null;
	}

	public long getRequestSize() {
		return ctx.getLength();
	}

}

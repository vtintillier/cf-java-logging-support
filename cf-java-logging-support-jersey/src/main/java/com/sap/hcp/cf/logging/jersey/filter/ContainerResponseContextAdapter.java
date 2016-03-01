package com.sap.hcp.cf.logging.jersey.filter;

import javax.ws.rs.container.ContainerResponseContext;

public class ContainerResponseContextAdapter implements ResponseContextAdapter {

	private final ContainerResponseContext ctx;

	public ContainerResponseContextAdapter(ContainerResponseContext responseContext) {
		this.ctx = responseContext;
	}
	public String getHeader(String headerName) {
		return ctx.getHeaderString(headerName);
	}

	public long getStatus() {
		return ctx.getStatus();
	}

	public long getLength() {
		int res = ctx.getLength();
		if (res < 0) {
			if (ctx.hasEntity()) {
				res = ctx.getEntity().toString().length();
			}
		}
		return res;
	}

}

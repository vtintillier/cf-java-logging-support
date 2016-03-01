package com.sap.hcp.cf.logging.jersey.filter;

import javax.ws.rs.client.ClientResponseContext;

public class ClientResponseContextAdapter implements ResponseContextAdapter {

	private final ClientResponseContext ctx;

	public ClientResponseContextAdapter(ClientResponseContext responseContext) {
		this.ctx = responseContext;
	}
	
	public String getHeader(String headerName) {
		return ctx.getHeaderString(headerName);
	}

	public long getStatus() {
		return ctx.getStatus();
	}

	public long getLength() {
		return ctx.getLength();
	}

}

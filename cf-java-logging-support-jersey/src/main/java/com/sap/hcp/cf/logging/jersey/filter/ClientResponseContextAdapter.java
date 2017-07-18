package com.sap.hcp.cf.logging.jersey.filter;

import javax.ws.rs.client.ClientResponseContext;

public class ClientResponseContextAdapter implements ResponseContextAdapter {

    private final ClientResponseContext ctx;

    public ClientResponseContextAdapter(ClientResponseContext responseContext) {
        ctx = responseContext;
    }

    @Override
    public String getHeader(String headerName) {
        return ctx.getHeaderString(headerName);
    }

    @Override
    public long getStatus() {
        return ctx.getStatus();
    }

    @Override
    public long getLength() {
        return ctx.getLength();
    }

}

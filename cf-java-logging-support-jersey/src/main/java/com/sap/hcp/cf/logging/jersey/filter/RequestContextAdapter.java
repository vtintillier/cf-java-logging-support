package com.sap.hcp.cf.logging.jersey.filter;

import java.net.URI;

import com.sap.hcp.cf.logging.common.request.HttpHeader;
import com.sap.hcp.cf.logging.common.request.RequestRecord.Direction;

public interface RequestContextAdapter {

    public String getHeader(String headerName);

    public void setHeader(String headerName, String headerValue);

    public String getName();

    public String getMethod();

    public String getUser();

    public URI getUri();

    public Direction getDirection();

    public long getRequestSize();

	public String getHeader(HttpHeader httpHeader);

}

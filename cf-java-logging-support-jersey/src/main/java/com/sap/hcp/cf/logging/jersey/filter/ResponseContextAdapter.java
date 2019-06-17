package com.sap.hcp.cf.logging.jersey.filter;

import com.sap.hcp.cf.logging.common.request.HttpHeader;

public interface ResponseContextAdapter {

	public String getHeader(HttpHeader header);

	public long getStatus();

	public long getLength();

}

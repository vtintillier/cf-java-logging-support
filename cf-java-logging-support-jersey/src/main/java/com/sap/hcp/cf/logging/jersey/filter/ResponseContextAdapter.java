package com.sap.hcp.cf.logging.jersey.filter;

public interface ResponseContextAdapter {

	public String getHeader(String headerName);
	public long getStatus();
	public long getLength();
	
}

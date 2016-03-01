package com.sap.hcp.cf.logging.common;

public interface Defaults {
	public String EMPTY = "";
	public String ZERO = "0";
	
	public String UNKNOWN = "-";
	public String COMPONENT_INDEX = ZERO;
	public String COMPONENT_TYPE = "application";	
	
	public String TYPE_REQUEST = "request";
	public String TYPE_LOG = "log";
	
	public LongValue RESPONSE_SIZE_B = new LongValue(-1);
	public LongValue REQUEST_SIZE_B = new LongValue(-1);
	public LongValue STATUS = new LongValue(200);
}

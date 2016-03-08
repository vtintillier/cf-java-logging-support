package com.sap.hcp.cf.logging.jersey.filter;

import javax.ws.rs.client.Invocation;

import org.glassfish.jersey.server.ContainerRequest;

import com.sap.hcp.cf.logging.common.HttpHeaders;
import com.sap.hcp.cf.logging.common.LogContext;

public class ClientRequestUtils {
	
	public static Invocation.Builder propagate(Invocation.Builder builder, javax.ws.rs.core.HttpHeaders reqHeaders) {
		if (LogContext.getCorrelationId() == null) {
			LogContext.initializeContext(reqHeaders != null ? reqHeaders.getHeaderString(HttpHeaders.CORRELATION_ID) : null);
		}
		builder.header(HttpHeaders.CORRELATION_ID, LogContext.getCorrelationId());
		return builder;
	}

}

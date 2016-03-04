package com.sap.hcp.cf.logging.jersey.filter;

import javax.ws.rs.client.Invocation;

import org.glassfish.jersey.server.ContainerRequest;

import com.sap.hcp.cf.logging.common.HttpHeaders;
import com.sap.hcp.cf.logging.common.LogContext;

public class ClientRequestUtils {
	
	public static Invocation.Builder propagate(Invocation.Builder builder, ContainerRequest req) {
		String correlationId = LogContext.getCorrelationId();
		if (correlationId == null) {
			correlationId = req.getHeaderString(HttpHeaders.CORRELATION_ID);
		}
		if (correlationId != null) {
			builder.header(HttpHeaders.CORRELATION_ID, correlationId);
		}
		return builder;
	}

}

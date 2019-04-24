package com.sap.hcp.cf.logging.jersey.filter;

import javax.ws.rs.client.Invocation;

import com.sap.hcp.cf.logging.common.HttpHeaders;
import com.sap.hcp.cf.logging.common.LogContext;
import com.sap.hcp.cf.logging.common.LogContextAdapter;

public class ClientRequestUtils {

    public static Invocation.Builder propagate(Invocation.Builder builder, javax.ws.rs.core.HttpHeaders reqHeaders) {
        if (LogContext.getCorrelationId() == null) {
            LogContext.initializeContext(reqHeaders != null ? reqHeaders.getHeaderString(HttpHeaders.CORRELATION_ID)
                                                            : null);
        }
        for (String header: HttpHeaders.PROPAGATED_HEADERS) {
            builder.header(header, LogContextAdapter.getValue(header));
        }
        return builder;
    }

}

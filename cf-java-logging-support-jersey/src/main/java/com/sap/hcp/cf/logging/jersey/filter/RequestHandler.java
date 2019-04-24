package com.sap.hcp.cf.logging.jersey.filter;

import java.net.URI;

import com.sap.hcp.cf.logging.common.*;

public class RequestHandler {
    final LogOptionalFieldsSettings logOptionalFieldsSettings;

    public RequestHandler() {
        String invokingClass = this.getClass().getName().toString();
        logOptionalFieldsSettings = new LogOptionalFieldsSettings(invokingClass);
    }

    public RequestRecord handle(RequestContextAdapter adapter) {

        propagateHeaders(adapter);

		boolean isSensitiveConnectionData = logOptionalFieldsSettings.isLogSensitiveConnectionData();
		boolean isLogRemoteUserField = logOptionalFieldsSettings.isLogRemoteUserField();
		boolean isLogRefererField = logOptionalFieldsSettings.isLogRefererField();
		RequestRecord lrec = RequestRecordBuilder.requestRecord(adapter.getName(), adapter.getDirection())
				.addTag(Fields.REQUEST, getValue(getRequestUri(adapter)))
				.addTag(Fields.METHOD, getValue(adapter.getMethod()))
				.addOptionalTag(isSensitiveConnectionData, Fields.REMOTE_IP, getValue(adapter.getUri().getAuthority()))
				.addOptionalTag(isSensitiveConnectionData, Fields.REMOTE_HOST, getValue(adapter.getUri().getHost()))
				.addOptionalTag(isSensitiveConnectionData, Fields.REMOTE_PORT,
						Integer.toString(adapter.getUri().getPort()))
				.addOptionalTag(isSensitiveConnectionData, Fields.X_FORWARDED_FOR,
						getHeader(adapter, HttpHeaders.X_FORWARDED_FOR))
				.addOptionalTag(isLogRemoteUserField, Fields.REMOTE_USER, getValue(adapter.getUser()))
				.addOptionalTag(isLogRefererField, Fields.REFERER, getHeader(adapter, HttpHeaders.REFERER))
				.addContextTag(Fields.REQUEST_ID, getHeader(adapter, HttpHeaders.X_VCAP_REQUEST_ID))
				.addValue(Fields.REQUEST_SIZE_B, new LongValue(adapter.getRequestSize())).build();

		lrec.start();
        return lrec;

    }

    private void propagateHeaders(RequestContextAdapter adapter) {
        /*
         * This might be an outgoing call and we may already have a
         * correlation id in the LogContext
         */
        if (LogContextAdapter.getValue(HttpHeaders.CORRELATION_ID) == null) {
            LogContext.initializeContext(getCorrelationIdFromHeader(adapter));
        }
        for (String header: HttpHeaders.PROPAGATED_HEADERS) {
            if (adapter.getHeader(header) == null) {
                adapter.setHeader(header, LogContextAdapter.getValue(header));
            }
        }
    }

    private String getValue(String value) {
        return value != null ? value : Defaults.UNKNOWN;
    }

    private String getCorrelationIdFromHeader(RequestContextAdapter adapter) {
        String cId = adapter.getHeader(HttpHeaders.CORRELATION_ID);
        if (cId == null || cId.length() == 0) {
            cId = adapter.getHeader(HttpHeaders.X_VCAP_REQUEST_ID);
        }
        return cId;
    }

    private String getHeader(RequestContextAdapter adapter, String headerName) {
        return getValue(adapter.getHeader(headerName));
    }

    private String getRequestUri(RequestContextAdapter adapter) {
        URI uri = adapter.getUri();
        StringBuilder sb = new StringBuilder(uri.getPath());
        if (uri.getQuery() != null) {
            sb.append("?").append(uri.getQuery());
        }
        return sb.toString();
    }
}

package com.sap.hcp.cf.logging.jersey.filter;

import static com.sap.hcp.cf.logging.common.RequestRecordConfigurator.to;

import java.net.URI;

import com.sap.hcp.cf.logging.common.Defaults;
import com.sap.hcp.cf.logging.common.Fields;
import com.sap.hcp.cf.logging.common.HttpHeaders;
import com.sap.hcp.cf.logging.common.LogContext;
import com.sap.hcp.cf.logging.common.LogOptionalFieldsSettings;
import com.sap.hcp.cf.logging.common.LongValue;
import com.sap.hcp.cf.logging.common.RequestRecord;

public class RequestHandler {
    final LogOptionalFieldsSettings logOptionalFieldsSettings;

    public RequestHandler() {
        String invokingClass = this.getClass().getName().toString();
        logOptionalFieldsSettings = new LogOptionalFieldsSettings(invokingClass);
    }

    public RequestRecord handle(RequestContextAdapter adapter) {

        /*
         * -- This might be an outgoing call and we may already have a
         * correlation -- id in the LogContext
         */
        String correlationId = LogContext.getCorrelationId();
        if (correlationId == null) {
            correlationId = getCorrelationIdFromHeader(adapter);
            LogContext.initializeContext(correlationId);
            /*
             * -- it was not in the header, then propagate
             */
            if (correlationId == null) {
                adapter.setHeader(HttpHeaders.CORRELATION_ID, LogContext.getCorrelationId());
            }
        }

        RequestRecord lrec = new RequestRecord(adapter.getName(), adapter.getDirection());
        lrec.start();

        addHeaders(adapter, lrec);

        return lrec;

    }

    private void addHeaders(RequestContextAdapter adapter, RequestRecord lrec) {

        lrec.addTag(Fields.REQUEST, getValue(getRequestUri(adapter)));
        lrec.addTag(Fields.METHOD, getValue(adapter.getMethod()));

        boolean isSensitiveConnectionData = logOptionalFieldsSettings.isLogSensitiveConnectionData();

        to(lrec).addOptionalTag(isSensitiveConnectionData, Fields.REMOTE_IP, getValue(adapter.getUri().getAuthority()))
                .addOptionalTag(isSensitiveConnectionData, Fields.REMOTE_HOST, getValue(adapter.getUri().getHost()))
                .addOptionalTag(isSensitiveConnectionData, Fields.REMOTE_PORT, Integer.toString(adapter.getUri()
                                                                                                       .getPort()))
                .addOptionalTag(isSensitiveConnectionData, Fields.X_FORWARDED_FOR, getHeader(adapter,
                                                                                             HttpHeaders.X_FORWARDED_FOR))
                .addOptionalTag(logOptionalFieldsSettings.isLogRemoteUserField(), Fields.REMOTE_USER, getValue(adapter
                                                                                                                      .getUser()))
                .addOptionalTag(logOptionalFieldsSettings.isLogRefererField(), Fields.REFERER, getHeader(adapter,
                                                                                                         HttpHeaders.REFERER));
        lrec.addContextTag(Fields.REQUEST_ID, getHeader(adapter, HttpHeaders.X_VCAP_REQUEST_ID));

        lrec.addValue(Fields.REQUEST_SIZE_B, new LongValue(adapter.getRequestSize()));

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

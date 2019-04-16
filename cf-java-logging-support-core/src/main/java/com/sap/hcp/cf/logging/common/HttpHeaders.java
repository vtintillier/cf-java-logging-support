package com.sap.hcp.cf.logging.common;

import java.util.*;

public interface HttpHeaders {

    public String CONTENT_LENGTH = "content-length";
    public String CONTENT_TYPE = "content-type";
    public String REFERER = "referer";
    public String X_FORWARDED_FOR = "x-forwarded-for";
    public String X_VCAP_REQUEST_ID = "x-vcap-request-id";
    public String CORRELATION_ID = "X-CorrelationID";
    public String TENANT_ID = "tenantid";

    public List<String> PROPAGATED_HEADERS = Arrays.asList(
        CORRELATION_ID,
        TENANT_ID
    );

    public Map<String, List<String>> ALIASES = new HashMap<String, List<String>>() {
        {
            put(CONTENT_LENGTH, Arrays.asList(CONTENT_LENGTH));
            put(CONTENT_TYPE, Arrays.asList((CONTENT_TYPE)));
            put(REFERER, Arrays.asList(REFERER));
            put(X_FORWARDED_FOR, Arrays.asList(X_FORWARDED_FOR));
            put(X_VCAP_REQUEST_ID, Arrays.asList(X_VCAP_REQUEST_ID));
            put(CORRELATION_ID, Arrays.asList(CORRELATION_ID, X_VCAP_REQUEST_ID));
            put(TENANT_ID, Arrays.asList(TENANT_ID));
        }
    };
}

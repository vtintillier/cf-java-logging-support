package com.sap.hcp.cf.logging.common;

import java.util.HashMap;
import java.util.Map;

public class LogContextAdapter {

    private static Map<String, String> HEADER_2_FIELDS = new HashMap<String, String>() {
        {
            put(HttpHeaders.CORRELATION_ID, Fields.CORRELATION_ID);
            put(HttpHeaders.TENANT_ID, Fields.TENANT_ID);
        }
    };

    public static String getValue(String header) {
        String field = getField(header);
        if (field != null) {
            return LogContext.get(field);
        } else {
            return Defaults.UNKNOWN;
        }
    }

    public static String getField(String header) {
        return HEADER_2_FIELDS.get(header);
    }
}

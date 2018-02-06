package com.sap.hcp.cf.logging.common;

public class RequestRecordConfigurator {

    private final RequestRecord requestRecord;

    private RequestRecordConfigurator(RequestRecord requestRecord) {
        this.requestRecord = requestRecord;
    }

    public static RequestRecordConfigurator to(RequestRecord requestRecord) {
        return new RequestRecordConfigurator(requestRecord);
    }

    public RequestRecordConfigurator addOptionalTag(boolean optionalFieldCanBeLogged, String fieldKey, String tag) {

        if (!optionalFieldCanBeLogged && tag != null) {
            requestRecord.addTag(fieldKey, Defaults.REDACTED);
        }

        if (!optionalFieldCanBeLogged && tag.equals(Defaults.UNKNOWN)) {
            requestRecord.addTag(fieldKey, tag);
        }

        if (optionalFieldCanBeLogged) {
            requestRecord.addTag(fieldKey, tag);
        }
        return this;
    }
}

package com.sap.hcp.cf.logback.encoder;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.sap.hcp.cf.logback.converter.api.LogbackContextFieldSupplier;
import com.sap.hcp.cf.logging.common.Defaults;
import com.sap.hcp.cf.logging.common.Fields;
import com.sap.hcp.cf.logging.common.Markers;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class BaseFieldSupplier implements LogbackContextFieldSupplier {

    @Override
    public Map<String, Object> map(ILoggingEvent event) {
        Map<String, Object> fields = new HashMap<>(6);
        fields.put(Fields.WRITTEN_AT, Instant.ofEpochMilli(event.getTimeStamp()).toString());
        fields.put(Fields.WRITTEN_TS, now());
        fields.put(Fields.TYPE, isRequestLog(event) ? Defaults.TYPE_REQUEST : Defaults.TYPE_LOG);
        fields.put(Fields.LEVEL, String.valueOf(event.getLevel()));
        fields.put(Fields.LOGGER, event.getLoggerName());
        if (!isRequestLog(event)) {
            fields.put(Fields.MSG, event.getFormattedMessage());
        }
        return fields;
    }

    private String now() {
        Instant now = Instant.now();
        long timestamp = now.getEpochSecond() * 1_000_000_000L + now.getNano();
        return String.valueOf(timestamp);
    }

    private boolean isRequestLog(ILoggingEvent event) {
        return Markers.REQUEST_MARKER.equals(event.getMarker());
    }

}

package com.sap.hcp.cf.logback.encoder;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.jr.ob.JSON;
import com.sap.hcp.cf.logback.converter.api.LogbackContextFieldSupplier;
import com.sap.hcp.cf.logging.common.Markers;
import com.sap.hcp.cf.logging.common.request.RequestRecord;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class RequestRecordFieldSupplier implements LogbackContextFieldSupplier {

    @Override
    public Map<String, Object> map(ILoggingEvent event) {
        if (!Markers.REQUEST_MARKER.equals(event.getMarker())) {
            return Collections.emptyMap();
        }
        if (event.getArgumentArray() == null) {
            try {
                return JSON.std.mapFrom(event.getFormattedMessage());
            } catch (IOException cause) {
                return Collections.emptyMap();
            }
        }
        Optional<RequestRecord> requestRecord = Stream.of(event.getArgumentArray()).filter(
                                                                                           o -> o instanceof RequestRecord)
                                                      .map(o -> (RequestRecord) o).findFirst();
        if (requestRecord.isPresent()) {
            RequestRecord record = requestRecord.get();
            return record.getFields().entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()
                                                                                                          .getValue()));
        }
        return Collections.emptyMap();
    }

}

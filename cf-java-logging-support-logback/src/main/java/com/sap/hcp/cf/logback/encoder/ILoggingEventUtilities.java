package com.sap.hcp.cf.logback.encoder;

import java.util.Map;

import com.sap.hcp.cf.logging.common.Markers;

import ch.qos.logback.classic.spi.ILoggingEvent;

public final class ILoggingEventUtilities {

    private ILoggingEventUtilities() {
    }

    public static boolean isRequestLog(ILoggingEvent event) {
        return Markers.REQUEST_MARKER.equals(event.getMarker());
    }

    public static Map<?, ?> getMap(ILoggingEvent event) {
        return event.getMDCPropertyMap();
    }

}

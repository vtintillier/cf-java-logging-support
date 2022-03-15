package com.sap.hcp.cf.logback.converter.api;

import java.util.Map;

import com.sap.hcp.cf.logging.common.serialization.ContextFieldSupplier;

import ch.qos.logback.classic.spi.ILoggingEvent;

@FunctionalInterface
public interface LogbackContextFieldSupplier extends ContextFieldSupplier {

    Map<String, Object> map(ILoggingEvent event);

    default Map<String, Object> get() {
        return map(null);
    }
}

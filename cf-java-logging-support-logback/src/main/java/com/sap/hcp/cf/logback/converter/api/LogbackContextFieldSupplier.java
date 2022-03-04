package com.sap.hcp.cf.logback.converter.api;

import com.sap.hcp.cf.logging.common.serialization.EventContextFieldSupplier;

import ch.qos.logback.classic.spi.ILoggingEvent;

@FunctionalInterface
public interface LogbackContextFieldSupplier extends EventContextFieldSupplier<ILoggingEvent> {

}

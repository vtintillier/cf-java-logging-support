package com.sap.hcp.cf.log4j2.converter.api;

import org.apache.logging.log4j.core.LogEvent;

import com.sap.hcp.cf.logging.common.serialization.EventContextFieldSupplier;

@FunctionalInterface
public interface Log4jContextFieldSupplier extends EventContextFieldSupplier<LogEvent> {

}

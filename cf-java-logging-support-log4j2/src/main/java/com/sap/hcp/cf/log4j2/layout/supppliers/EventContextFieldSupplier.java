package com.sap.hcp.cf.log4j2.layout.supppliers;

import java.util.Map;

import org.apache.logging.log4j.core.LogEvent;

import com.sap.hcp.cf.log4j2.converter.api.Log4jContextFieldSupplier;
import com.sap.hcp.cf.logging.common.serialization.AbstractContextFieldSupplier;

public class EventContextFieldSupplier extends AbstractContextFieldSupplier<LogEvent> implements
                                       Log4jContextFieldSupplier {

    @Override
    protected Map<String, String> getContextMap(LogEvent event) {
        return event.getContextData().toMap();
    }

    @Override
    protected Object[] getParameterArray(LogEvent event) {
        return LogEventUtilities.getParameterArray(event);
    }

}

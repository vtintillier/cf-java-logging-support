package com.sap.hcp.cf.logback.encoder;

import java.util.HashMap;
import java.util.Map;

import com.sap.hcp.cf.logback.converter.api.LogbackContextFieldSupplier;
import com.sap.hcp.cf.logging.common.customfields.CustomField;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class EventContextFieldSupplier implements LogbackContextFieldSupplier {

    @Override
    public Map<String, Object> map(ILoggingEvent event) {
        Map<String, Object> result = new HashMap<>();
        result.putAll(event.getMDCPropertyMap());
        Object[] arguments = event.getArgumentArray();
        if (arguments != null) {
            for (Object argument: arguments) {
                if (argument instanceof CustomField) {
                    CustomField customField = (CustomField) argument;
                    result.put(customField.getKey(), customField.getValue());
                }
            }
        }
        return result;
    }

}

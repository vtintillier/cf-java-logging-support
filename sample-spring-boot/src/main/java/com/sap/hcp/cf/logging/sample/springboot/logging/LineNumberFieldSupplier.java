package com.sap.hcp.cf.logging.sample.springboot.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.sap.hcp.cf.logback.converter.api.LogbackContextFieldSupplier;

import java.util.HashMap;
import java.util.Map;

public class LineNumberFieldSupplier implements LogbackContextFieldSupplier {
    @Override
    public Map<String, Object> map(ILoggingEvent event) {
        StackTraceElement[] stackTraceElements = event.getCallerData();
        if (stackTraceElements != null && stackTraceElements.length > 0) {
            HashMap<String, Object> result = new HashMap<>();
            result.put("line_number", stackTraceElements[0].getLineNumber());
            result.put("method", stackTraceElements[0].getMethodName());
            return result;
        }
        return null;
    }
}

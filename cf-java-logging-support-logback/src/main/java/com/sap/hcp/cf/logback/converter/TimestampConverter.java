package com.sap.hcp.cf.logback.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * This is a simple {@link ClassicConverter} implementation that print the
 * timestamp as a long in nano second resolution.
 */
public class TimestampConverter extends ClassicConverter {

    public static final String WORD = "tstamp";

    @Override
    public String convert(ILoggingEvent event) {
        StringBuilder appendTo = new StringBuilder();
        appendTo.append(System.currentTimeMillis() * 1000000);
        return appendTo.toString();
    }

    @Override
    public void start() {
        super.start();
    }
}

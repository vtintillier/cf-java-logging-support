package com.sap.hcp.cf.log4j2.converter;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(name = "TimestampConverter", category = "Converter")
@ConverterKeys({ "tstamp" })
public class TimestampConverter extends LogEventPatternConverter {

    public static final String WORD = "tstamp";

    public TimestampConverter(String[] options) {
        super(WORD, WORD);
    }

    public static TimestampConverter newInstance(final String[] options) {
        return new TimestampConverter(options);
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        toAppendTo.append(System.nanoTime());
    }

}

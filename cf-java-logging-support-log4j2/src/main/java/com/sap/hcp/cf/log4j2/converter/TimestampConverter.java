package com.sap.hcp.cf.log4j2.converter;

import java.time.Instant;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

@Plugin(name = "TimestampConverter", category = "Converter")
@ConverterKeys({ "tstamp" })
/**
 * This is a simple {@link LogEventPatternConverter} implementation that prints
 * the timestamp as a long in nano second resolution. Note: nano second
 * precision is only available from Java 9 or newer. Java 8 will only have milli
 * seconds.
 */
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
		Instant now = Instant.now();
		long timestamp = now.getEpochSecond() * 1_000_000_000L + now.getNano();
		toAppendTo.append(timestamp);
    }

}

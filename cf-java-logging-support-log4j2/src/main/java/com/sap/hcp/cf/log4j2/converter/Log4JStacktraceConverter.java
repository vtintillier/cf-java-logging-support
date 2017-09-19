package com.sap.hcp.cf.log4j2.converter;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

import com.sap.hcp.cf.logging.common.converter.StacktraceConverter;

@Plugin(name = "Log4JStacktraceConverter", category = "Converter")
@ConverterKeys({ "stacktrace" })
public class Log4JStacktraceConverter extends LogEventPatternConverter {
    public static final String WORD = "stacktrace";

    public Log4JStacktraceConverter(String[] options) {
        super(WORD, WORD);
    }

    public static Log4JStacktraceConverter newInstance(final String[] options) {
        return new Log4JStacktraceConverter(options);
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        StacktraceConverter.CONVERTER.convert(event.getThrown(), toAppendTo);
    }

}

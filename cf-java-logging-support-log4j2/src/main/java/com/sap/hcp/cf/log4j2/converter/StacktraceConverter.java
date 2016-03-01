package com.sap.hcp.cf.log4j2.converter;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

import com.sap.hcp.cf.logging.common.converter.DefaultStacktraceConverter;

@Plugin(name="StacktraceConverter", category="Converter")
@ConverterKeys({"stacktrace"})
public class StacktraceConverter extends LogEventPatternConverter  {

	public static final String WORD = "stacktrace";
	private final DefaultStacktraceConverter converter = new DefaultStacktraceConverter();
	
	public StacktraceConverter(String[] options) {
		super(WORD, WORD);
	}
	
	public static StacktraceConverter newInstance(final String[] options) {
		return new StacktraceConverter(options);
	}
	
	@Override
	public void format(LogEvent event, StringBuilder toAppendTo) {
		converter.convert(event.getThrown(), toAppendTo);
	}

}

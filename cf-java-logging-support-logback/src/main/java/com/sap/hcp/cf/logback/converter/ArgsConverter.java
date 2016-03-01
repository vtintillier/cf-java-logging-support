package com.sap.hcp.cf.logback.converter;

import com.sap.hcp.cf.logging.common.converter.DefaultArgsConverter;
import com.sap.hcp.cf.logging.common.customfields.CustomField;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * This is a simple {@link ClassicConverter} implementation that converts key/value pairs
 * stored in {@link CustomField} instances which have been passed as arguments.
 * <p>
 * We allow to types of addition to a log message, either <i>embedded</i>, i.e. the key/value
 * pairs appear as a list of JSON fields in the message, or as a nested object where the field
 * name has been specified as an option to this converter.
 */
public class ArgsConverter extends ClassicConverter {

	public static final String WORD = "args";

	private final DefaultArgsConverter converter = new DefaultArgsConverter();
	
	@Override
	public String convert(ILoggingEvent event) {
		StringBuilder appendTo = new StringBuilder();
		converter.convert(event.getArgumentArray(), appendTo);
		return appendTo.toString();
	}

	@Override
	public void start() {
		converter.setFieldName(getFirstOption());
		super.start();
	}
}
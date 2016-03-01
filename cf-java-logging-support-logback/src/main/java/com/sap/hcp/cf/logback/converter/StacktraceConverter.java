package com.sap.hcp.cf.logback.converter;

import com.sap.hcp.cf.logging.common.converter.DefaultStacktraceConverter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;

public class StacktraceConverter extends ClassicConverter {

	public static final String WORD = "stacktrace";
	private final DefaultStacktraceConverter converter = new DefaultStacktraceConverter();

	@Override
	public String convert(ILoggingEvent event) {
		StringBuilder appendTo = new StringBuilder();
		IThrowableProxy tProxy = event.getThrowableProxy();
		if (tProxy != null && ThrowableProxy.class.isAssignableFrom(tProxy.getClass())) {
			converter.convert(((ThrowableProxy)tProxy).getThrowable(),appendTo);
			return appendTo.toString();
		}
		return null;
	}

}

package com.sap.hcp.cf.logback.converter;

import java.util.List;

import com.sap.hcp.cf.logging.common.LogContext;
import com.sap.hcp.cf.logging.common.converter.DefaultPropertiesConverter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * A simple {@link ClassicConverter} implementation that converts key/value pairs from the {@link org.slf4j.MDC}.
 * These key/value pairs are embedded in the JSON message, i.e. the appear as fields at the top-level
 * JSON object.
 * <p>
 * There are two exceptions to this:
 * <ol>
 * 	<li>The predefined keys from {@link com.sap.hcp.cf.logging.common.Fields}</li>
 * 	<li>The list of key names that have been passed in as options.</li>
 * </ol>
 *
 */
public class ContextPropsConverter extends ClassicConverter {

	public static final String WORD = "ctxp";
	private final DefaultPropertiesConverter converter = new DefaultPropertiesConverter();
	
	@Override
	public String convert(ILoggingEvent event) {
		StringBuilder appendTo = new StringBuilder();
		LogContext.loadContextFields();
		converter.convert(event.getMDCPropertyMap(), appendTo);
		return appendTo.toString();
	}

	@Override
	public void start() {
		List<String> exclusionList = getOptionList();
		if (exclusionList != null) {
			converter.setExclusions(exclusionList);
		}
		super.start();
	}
}
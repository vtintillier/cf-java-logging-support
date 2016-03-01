package com.sap.hcp.cf.log4j2.converter;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

import com.sap.hcp.cf.logging.common.converter.DefaultMessageConverter;

/**
 * A simple {@link LogEventPatternConverter} that converts a message into a JSON message.
 * <p>
 * The main point are that we may need to do escaping and/or flattening depending on the
 * context. <i>Escaping</i> means that we write the message as a quoted string and thus need to
 * <i>escape</i> properly within the message string. If a message is <i>flattened</i>, objects or
 * arrays are turned into a list of fields or values.
 *
 */
@Plugin(name="JsonMessageConverter", category="Converter")
@ConverterKeys({"jsonmsg"})
public class JsonMessageConverter extends LogEventPatternConverter  {

	public static final String WORD = "jsonmsg";
	public static final String OPT_ESCAPE = "escape";
	public static final String OPT_FLATTEN = "flatten";
	
	private final DefaultMessageConverter converter = new DefaultMessageConverter();
	
	public JsonMessageConverter(String[] options) {
		super(WORD, WORD);
		if (options != null) {
			for (String option : options) {
				if (OPT_FLATTEN.equalsIgnoreCase(option)) {
					converter.setFlatten(true);
				}
				else if (OPT_ESCAPE.equalsIgnoreCase(option)) {
					converter.setEscape(true);
				}
			}
		}
	}
	
	public static JsonMessageConverter newInstance(final String[] options) {
		return new JsonMessageConverter(options);
	}
	
	@Override
	public void format(LogEvent event, StringBuilder toAppendTo) {
		converter.convert(event.getMessage().getFormattedMessage(), toAppendTo);		
	}	
}

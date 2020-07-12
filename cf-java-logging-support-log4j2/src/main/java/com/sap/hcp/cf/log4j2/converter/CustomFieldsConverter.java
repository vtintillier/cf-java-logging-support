package com.sap.hcp.cf.log4j2.converter;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.ReadOnlyStringMap;

import com.sap.hcp.cf.logging.common.converter.DefaultCustomFieldsConverter;
import com.sap.hcp.cf.logging.common.customfields.CustomField;

/**
 * This is a simple {@link LogEventPatternConverter} implementation that
 * converts key/value pairs stored in {@link CustomField} instances which have
 * been passed as arguments.
 * <p>
 * We allow to types of addition to a log message, either <i>embedded</i>, i.e.
 * the key/value pairs appear as a list of JSON fields in the message, or as a
 * nested object where the field name has been specified as an option to this
 * converter.
 */
@Plugin(name = "ArgsConverter", category = PatternConverter.CATEGORY)
@ConverterKeys({ "cf" })
public class CustomFieldsConverter extends LogEventPatternConverter {

	public static final String WORD = "cf";

	private final List<String> customFieldMdcKeyNames;
	private DefaultCustomFieldsConverter converter = new DefaultCustomFieldsConverter();

	public CustomFieldsConverter(String[] options) {
		super(WORD, WORD);

		customFieldMdcKeyNames = options == null ? emptyList() : unmodifiableList(asList(options));
		converter.setCustomFieldKeyNames(customFieldMdcKeyNames);
	}
	
	public static CustomFieldsConverter newInstance(final String[] options) {
		return new CustomFieldsConverter(options);
	}

	void setConverter(DefaultCustomFieldsConverter converter) {
		this.converter = converter;
	}
	
	@Override
	public void format(LogEvent event, StringBuilder appendTo) {
		converter.convert(appendTo, getCustomFieldsFromMdc(event), getMessageParameters(event));
	}

	private Object[] getMessageParameters(LogEvent event) {
		Message message = event.getMessage();
		return message == null ? null : message.getParameters();
	}

	private Map<String, String> getCustomFieldsFromMdc(LogEvent event) {
		ReadOnlyStringMap contextData = event.getContextData();
		return contextData != null ? contextData.toMap() : Collections.emptyMap();
	}
}

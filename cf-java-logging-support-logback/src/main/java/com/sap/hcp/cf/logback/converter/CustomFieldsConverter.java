package com.sap.hcp.cf.logback.converter;

import java.util.Map;

import com.sap.hcp.cf.logging.common.LogContext;
import com.sap.hcp.cf.logging.common.converter.DefaultCustomFieldsConverter;
import com.sap.hcp.cf.logging.common.customfields.CustomField;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * This is a simple {@link ClassicConverter} implementation that converts
 * key/value pairs stored in {@link CustomField} instances which have been
 * passed as arguments.
 * <p>
 * We allow two types of additions to a log message, either <i>embedded</i>,
 * i.e. the key/value pairs appear as a list of JSON fields in the message, or
 * as a nested object where the field name has been specified as an option to
 * this converter.
 */

public class CustomFieldsConverter extends ClassicConverter {

	public static final String WORD = "args";

	private DefaultCustomFieldsConverter converter = new DefaultCustomFieldsConverter();
	private CustomFieldsAdapter customFieldsAdapter = new CustomFieldsAdapter();
	

	void setConverter(DefaultCustomFieldsConverter converter) {
		this.converter = converter;
	}

	void setCustomFieldsAdapter(CustomFieldsAdapter customFieldsAdapter) {
		this.customFieldsAdapter = customFieldsAdapter;
	}
	
	@Override
	public String convert(ILoggingEvent event) {
		Object[] argumentArray = event.getArgumentArray();
		Map<String, String> mdcCustomFields = getMdcCustomFields(event);
		StringBuilder appendTo = new StringBuilder();
		converter.convert(appendTo, mdcCustomFields, argumentArray);
		return appendTo.toString();
	}

	private Map<String, String> getMdcCustomFields(ILoggingEvent event) {
		LogContext.loadContextFields();
		Map<String, String> mdcPropertyMap = event.getMDCPropertyMap();
		return customFieldsAdapter.selectCustomFields(mdcPropertyMap);
	}

	@Override
	public void start() {
		converter.setFieldName(getFirstOption());
		customFieldsAdapter.initialize(getContext());
		super.start();
	}
}

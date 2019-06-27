package com.sap.hcp.cf.logback.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.hcp.cf.logging.common.LogContext;
import com.sap.hcp.cf.logging.common.converter.DefaultCustomFieldsConverter;
import com.sap.hcp.cf.logging.common.customfields.CustomField;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;

/**
 * This is a simple {@link ClassicConverter} implementation that converts
 * key/value pairs stored in {@link CustomField} instances which have been
 * passed as arguments.
 * <p>
 * We allow to types of addition to a log message, either <i>embedded</i>, i.e.
 * the key/value pairs appear as a list of JSON fields in the message, or as a
 * nested object where the field name has been specified as an option to this
 * converter.
 */

public class CustomFieldsConverter extends ClassicConverter {

	public static final String OPTION_MDC_CUSTOM_FIELDS = "customFieldMdcKeyNames";
	public static final String WORD = "args";

	private DefaultCustomFieldsConverter converter = new DefaultCustomFieldsConverter();

	private List<String> customFieldMdcKeyNames = Collections.emptyList();

	void setConverter(DefaultCustomFieldsConverter converter) {
		this.converter = converter;
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
		HashMap<String, String> result = new HashMap<>();
		for (Map.Entry<String, String> current : mdcPropertyMap.entrySet()) {
			if (customFieldMdcKeyNames.contains(current.getKey())) {
				result.put(current.getKey(), current.getValue());
			}
		}
		return result;
	}

	@Override
    public void start() {
        converter.setFieldName(getFirstOption());
		customFieldMdcKeyNames = getCustomFieldMdcKeyNames();
        super.start();
    }

	private List<String> getCustomFieldMdcKeyNames() {
		Context context = getContext();
		if (context == null) {
			return Collections.emptyList();
		}
		Object object = context.getObject(OPTION_MDC_CUSTOM_FIELDS);
		if (object instanceof List) {
			List<?> list = (List<?>) object;
			ArrayList<String> result = new ArrayList<>(list.size());
			for (Object current : list) {
				result.add(current.toString());
			}
			return result;
		}
		return Collections.emptyList();
	}
}

package com.sap.hcp.cf.logging.common.converter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONComposer;
import com.fasterxml.jackson.jr.ob.JSONObjectException;
import com.fasterxml.jackson.jr.ob.comp.ArrayComposer;
import com.fasterxml.jackson.jr.ob.comp.ObjectComposer;
import com.sap.hcp.cf.logging.common.customfields.CustomField;

public class DefaultCustomFieldsConverter {

	private String fieldName = null;
	private boolean embed = true;
	private List<String> customFieldKeyNames;

	public void setFieldName(String fieldName) {
		if (fieldName != null) {
			this.fieldName = fieldName;
			embed = false;
		}
	}

	public void setCustomFieldKeyNames(List<String> customFieldKeyNames) {
		this.customFieldKeyNames = customFieldKeyNames;
	}

    private static class LoggerHolder {
    	static final Logger LOG = LoggerFactory.getLogger(LoggerHolder.class.getEnclosingClass());
    }

	public void convert(StringBuilder appendTo, Map<String, String> mdcPropertiesMap, Object... arguments) {
		if (customFieldKeyNames.isEmpty()) {
			return;
		}
		Map<String, CustomField> customFields = getRegisteredCustomFields(arguments);
		Map<String, String> mdcCustomFields = getRegisteredMdcCustomFields(mdcPropertiesMap);
		if (!customFields.isEmpty() || !mdcCustomFields.isEmpty()) {
			try {
				ArrayComposer<ObjectComposer<JSONComposer<String>>> oc = startJson(appendTo);
				addCustomFields(oc, customFields, mdcCustomFields);
				finishJson(oc, appendTo);
			} catch (Exception ex) {
				/* -- avoids substitute logger warnings on startup -- */
				LoggerHolder.LOG.error("Conversion failed ", ex);
			}
		}
	}

	private Map<String, String> getRegisteredMdcCustomFields(Map<String, String> mdcPropertiesMap) {
		if (mdcPropertiesMap.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, String> mdcCustomFields = new HashMap<>(mdcPropertiesMap.size());
		for (Map.Entry<String, String> current : mdcPropertiesMap.entrySet()) {
			if (customFieldKeyNames.contains(current.getKey())) {
				mdcCustomFields.put(current.getKey(), current.getValue());
			}
		}
		return mdcCustomFields;
	}

	private Map<String, CustomField> getRegisteredCustomFields(Object... arguments) {
		if (arguments == null) {
			return Collections.emptyMap();
		}
		Map<String, CustomField> result = new HashMap<>();
		for (Object current : arguments) {
			if (current instanceof CustomField) {
				CustomField field = (CustomField) current;
				if (customFieldKeyNames.contains(field.getKey())) {
					result.put(field.getKey(), field);
				}
			}
		}
		return result;
	}

	private ArrayComposer<ObjectComposer<JSONComposer<String>>> startJson(StringBuilder appendTo)
			throws IOException, JSONObjectException, JsonProcessingException {
		if (!embed) {
			appendTo.append(JSON.std.asString(fieldName)).append(":");
		}
		/*
		 * -- no matter whether we embed or not, it seems easier to compose -- a JSON
		 * object from the key/value pairs. -- if we embed that object, we simply chop
		 * off the outermost curly braces.
		 */
		return JSON.std.composeString().startObject().startArrayField("string");
	}

	private void addCustomFields(ArrayComposer<ObjectComposer<JSONComposer<String>>> oc,
			Map<String, CustomField> customFields, Map<String, String> mdcCustomFields)
			throws IOException, JsonProcessingException {
		for (int i = 0; i < customFieldKeyNames.size(); i++) {
			String key = customFieldKeyNames.get(i);
			String value = mdcCustomFields.get(key);
			// Let argument CustomField take precedence over MDC
			CustomField field = customFields.get(key);
			if (field != null) {
				value = field.getValue();
			}
			if (value != null) {
				oc.startObject().put("k", key).put("v", value).put("i", i).end();
			}
		}
	}

	private void finishJson(ArrayComposer<ObjectComposer<JSONComposer<String>>> oc, StringBuilder appendTo)
			throws IOException, JsonProcessingException {
		ObjectComposer<JSONComposer<String>> end = oc.end();
		String result = end.end().finish().trim();
		if (embed) {
			/* -- chop off curly braces -- */
			appendTo.append(result.substring(1, result.length() - 1));
		} else {
			appendTo.append(result);
		}
	}
}

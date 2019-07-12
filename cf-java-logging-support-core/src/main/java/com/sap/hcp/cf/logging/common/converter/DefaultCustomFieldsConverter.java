package com.sap.hcp.cf.logging.common.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONComposer;
import com.fasterxml.jackson.jr.ob.comp.ObjectComposer;
import com.sap.hcp.cf.logging.common.customfields.CustomField;

public class DefaultCustomFieldsConverter {

    private String fieldName = null;
    private boolean embed = true;

    public void setFieldName(String fieldName) {
        if (fieldName != null) {
            this.fieldName = fieldName;
            embed = false;
        }
    }

	public void convert(StringBuilder appendTo, Map<String, String> mdcCustomFields, Object... arguments) {
		List<CustomField> customFields = getCustomFields(arguments);
		if (!customFields.isEmpty() || !mdcCustomFields.isEmpty()) {
			try {
				if (!embed) {
					appendTo.append(JSON.std.asString(fieldName)).append(":");
				}
				/*
				 * -- no matter whether we embed or not, it seems easier to
				 * compose -- a JSON object from the key/value pairs. -- if we
				 * embed that object, we simply chop off the outermost curly
				 * braces.
				 */
				ObjectComposer<JSONComposer<String>> oc = JSON.std.composeString().startObject();
				for (CustomField cf : customFields) {
					oc.putObject(cf.getKey(), cf.getValue());
				}
				for (Map.Entry<String, String> mdcField : mdcCustomFields.entrySet()) {
					oc.put(mdcField.getKey(), mdcField.getValue());
				}
				String result = oc.end().finish().trim();
				if (embed) {
					/* -- chop off curly braces -- */
					appendTo.append(result.substring(1, result.length() - 1));
				} else {
					appendTo.append(result);
				}
			} catch (Exception ex) {
				/* -- avoids substitute logger warnings on startup -- */
				LoggerFactory.getLogger(DefaultCustomFieldsConverter.class).error("Conversion failed ", ex);
			}
		}
	}

	private List<CustomField> getCustomFields(Object[] arguments) {
		if (arguments == null || arguments.length == 0) {
			return Collections.emptyList();
		}
		List<CustomField> customFields = new ArrayList<CustomField>();
		for (Object argument : arguments) {
			if (argument instanceof CustomField) {
				customFields.add((CustomField) argument);
			}
		}
		return customFields;
	}
}

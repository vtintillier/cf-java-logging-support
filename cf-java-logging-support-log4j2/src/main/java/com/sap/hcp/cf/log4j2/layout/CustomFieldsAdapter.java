package com.sap.hcp.cf.log4j2.layout;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.sap.hcp.cf.logging.common.LogContext;

public class CustomFieldsAdapter {

	private List<CustomField> customFields;

	public CustomFieldsAdapter(CustomField... customFields) {
		this.customFields = customFields == null ? emptyList() : asList(customFields);
	}

	public List<String> getCustomFieldKeyNames() {
		List<String> result = new ArrayList<>(customFields.size());
		for (CustomField customField : customFields) {
			result.add(customField.getKey());
		}
		return result;
	}

	public List<String> getExcludedFieldKeyNames() {
		Collection<String> contextFieldsKeys = LogContext.getContextFieldsKeys();
		List<String> result = new ArrayList<>(customFields.size());
		for (CustomField customField : customFields) {
			if (!customField.isRetainOriginal() && !contextFieldsKeys.contains(customField.getKey())) {
				result.add(customField.getKey());
			}
		}
		return result;
	}

}

package com.sap.hcp.cf.logback.converter;

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.hcp.cf.logging.common.LogContext;

import ch.qos.logback.core.Context;

public class CustomFieldsAdapter {

	public static final String OPTION_MDC_CUSTOM_FIELDS = "customFieldMdcKeyNames";
	public static final String OPTION_MDC_RETAINED_FIELDS = "retainFieldMdcKeyNames";

	private List<String> customFieldMdcKeyNames = emptyList();
	private List<String> customFieldExclusions = emptyList();

	public void initialize(Context context) {
		if (context == null) {
			return;
		}
		customFieldExclusions = calculateExclusions(context);
		customFieldMdcKeyNames = getListItemsAsStrings(context, OPTION_MDC_CUSTOM_FIELDS);
	}

	private List<String> calculateExclusions(Context context) {
		List<String> candidates = getListItemsAsStrings(context, OPTION_MDC_CUSTOM_FIELDS);
		candidates.removeAll(getListItemsAsStrings(context, OPTION_MDC_RETAINED_FIELDS));
		candidates.removeAll(LogContext.getContextFieldsKeys());
		return unmodifiableList(candidates);
	}

	private List<String> getListItemsAsStrings(Context context, String key) {
		Object object = context.getObject(key);
		if (object instanceof Collection) {
			Collection<?> list = (Collection<?>) object;
			ArrayList<String> listItems = new ArrayList<>(list.size());
			for (Object current : list) {
				listItems.add(current.toString());
			}
			return listItems;
		}
		return emptyList();
	}
	
	public List<String> getCustomFieldExclusions() {
		return customFieldExclusions;
	}

	public Map<String, String> selectCustomFields(Map<String, String> in) {
		if (in == null) {
			return Collections.emptyMap();
		}
		HashMap<String, String> result = new HashMap<>(in.size());
		for (Map.Entry<String, String> current : in.entrySet()) {
			if (customFieldMdcKeyNames.contains(current.getKey())) {
				result.put(current.getKey(), current.getValue());
			}
		}
		return result;
	}
}

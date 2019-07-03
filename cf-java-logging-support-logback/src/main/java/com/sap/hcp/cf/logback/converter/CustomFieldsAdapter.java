package com.sap.hcp.cf.logback.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.core.Context;

public class CustomFieldsAdapter {

	public static final String OPTION_MDC_CUSTOM_FIELDS = "customFieldMdcKeyNames";

	private List<String> customFieldMdcKeyNames = Collections.emptyList();

	public void initialize(Context context) {
		if (context == null) {
			return;
		}
		Object object = context.getObject(OPTION_MDC_CUSTOM_FIELDS);
		if (object instanceof List) {
			List<?> list = (List<?>) object;
			customFieldMdcKeyNames = new ArrayList<>(list.size());
			for (Object current : list) {
				customFieldMdcKeyNames.add(current.toString());
			}
		}
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

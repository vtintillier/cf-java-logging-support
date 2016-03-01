package com.sap.hcp.cf.logging.common.converter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.MDC;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;
import com.sap.hcp.cf.logging.common.converter.DefaultArgsConverter;
import com.sap.hcp.cf.logging.common.converter.DefaultMessageConverter;
import com.sap.hcp.cf.logging.common.converter.DefaultPropertiesConverter;
import com.sap.hcp.cf.logging.common.converter.DefaultStacktraceConverter;
import com.sap.hcp.cf.logging.common.customfields.CustomField;

public abstract class AbstractConverterTest {
	protected static final String PREFIX = "prefix";
	protected static final String EMPTY = "";
	protected static final String SOME_KEY = "some_key";
	protected static final String SOME_VALUE = "some value";
	protected static final String STRANGE_SEQ = "}{:\",\"";
	protected static final String SOME_OTHER_KEY = "some_other_key";
	protected static final String SOME_OTHER_VALUE = "some other value";
	protected static final String TEST_MSG_NO_ARGS = "This is a test ";
	protected static final Object[] NO_ARGS = new Object[0];
	protected static final Object[] NON_CUSTOM_ARGS = new Object[] { new String("standard") };
	
	protected String formatMsg(DefaultMessageConverter mc, String msg) {
		StringBuilder sb = new StringBuilder();
		mc.convert(msg, sb);
		return sb.toString();
	}
	
	protected String formatProps(DefaultPropertiesConverter pc) {
		StringBuilder sb = new StringBuilder();
		pc.convert(MDC.getCopyOfContextMap(), sb);
		return sb.toString();
	}
	
	protected String formatArgs(DefaultArgsConverter ac, Object[] args) {
		StringBuilder sb = new StringBuilder();
		ac.convert(args, sb);
		return sb.toString();
	}
	
	protected String formatStacktrace(DefaultStacktraceConverter dstc, Throwable t) {
		StringBuilder sb = new StringBuilder();
		dstc.convert(t, sb);
		return sb.toString();
	}
	
	protected Map<String, Object> makeMap(CustomField[] custFields) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (CustomField cf : custFields) {
			map.put(cf.getKey(), cf.getValue());
		}
		return map;
	}
	
	protected Map<String, Object> makeMap(String[] keys) {
		Map<String, Object> map = new HashMap<String, Object>();
		for (String key : keys) {
			map.put(key, MDC.get(key));
		}
		return map;
	}

	protected Map<String, Object> mdcMap() {
		return mdcMap(null);
	}
	
 	protected Map<String, Object> mdcMap(String[] exclusions) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> exclusionList;
		if (exclusions == null) {
			exclusionList = Arrays.asList(new String[0]);
		}
		else {
			exclusionList = Arrays.asList(exclusions);
		}
		for (Entry<String, String> t : MDC.getCopyOfContextMap().entrySet()) {
			if (!exclusionList.contains(t.getKey())) {
				result.put(t.getKey(), t.getValue());
			}
		}
		return result;
	}

 	protected Object arrayElem(String serialized, int i) throws JSONObjectException, IOException {
 		return arrayFrom(serialized)[i];
 	}
 	
 	protected Object[]arrayFrom(String serialized) throws JSONObjectException, IOException {
 		return JSON.std.arrayFrom(serialized);
 	}
 	
	protected Map<String, Object> mapFrom(String serialized) throws JSONObjectException, IOException {
		return mapFrom(serialized, true);
	}
	
	protected Map<String, Object> mapFrom(String serialized, boolean wrap) throws JSONObjectException, IOException {
		if (wrap) {
			return JSON.std.mapFrom("{" + serialized + "}");
		}
		else {
			return JSON.std.mapFrom(serialized);
		}
	}
}

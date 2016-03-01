package com.sap.hcp.cf.logging.common.converter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jr.ob.JSON;

public class DefaultMessageConverter {
		
	private boolean flatten = false;
	private boolean escape = false;
	private static Pattern OBJ_PATTERN = Pattern.compile("\\s*\\{([^}]+)}");
	private static Pattern ARRAY_PATTERN = Pattern.compile("\\s*\\[([^\\]]+)]");
	
	public boolean isFlatten() {
		return flatten;
	}
	public void setFlatten(boolean flatten) {
		this.flatten = flatten;
	}
	public boolean isEscape() {
		return escape;
	}
	public void setEscape(boolean escape) {
		this.escape = escape;
	}

	public void convert(String message, StringBuilder appendTo) {
		if (message != null) {
			String result;
			if (flatten) {
				result = flattenMsg(message);
			}
			else {
				result = message;
			}
			if (escape) {
				try {
					appendTo.append(JSON.std.asString(result));
				} catch (Exception ex) {
					/* -- avoids substitute logger warnings on startup -- */
					LoggerFactory.getLogger(DefaultMessageConverter.class).error("Conversion failed ", ex);
					appendTo.append(result);
				}
			}
			else {
				appendTo.append(result);
			}		
		}
	}
		
	private String flattenMsg(String msg) {
			Matcher m = OBJ_PATTERN.matcher(msg);
			if (m.matches()) {
				return m.group(1);
			}
			m = ARRAY_PATTERN.matcher(msg);
			if (m.matches()) {
				return m.group(1);
			}
			return msg;
		}
		
}

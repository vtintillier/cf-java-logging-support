package com.sap.hcp.cf.logging.common;

public class PatternUtils {


	public static String JSON_FIELD(String name, String value, boolean quoteValue, boolean withComma) {
		StringBuffer buf = new StringBuffer("\"");
		buf.append(name).append("\":");
		if (quoteValue) {
			buf.append("\"");
		}
		buf.append(value);
		if (quoteValue) {
			buf.append("\"");
		}
		if (withComma) {
			buf.append(",");
		}
		return buf.toString();
	}
}

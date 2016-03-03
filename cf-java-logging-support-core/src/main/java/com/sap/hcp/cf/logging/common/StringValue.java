package com.sap.hcp.cf.logging.common;

/**
 * A {@link Value} implementation representing a <i>string</i>.
 *
 */
public class StringValue implements Value {

	public static final String UNDEFINED = "-";
	
	private String value;
	
	public StringValue(final String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public Object getValue() {
		return String.valueOf(value);
	}

	public long asLong() {
		return Long.parseLong(value);
	}

	public double asDouble() {
		return Double.parseDouble(value);
	}

	public String asString() {
		return value;
	}
}

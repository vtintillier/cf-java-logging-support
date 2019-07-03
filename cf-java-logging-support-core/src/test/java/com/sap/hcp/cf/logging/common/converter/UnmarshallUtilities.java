package com.sap.hcp.cf.logging.common.converter;

import java.util.Map;

import com.fasterxml.jackson.jr.ob.JSON;

public final class UnmarshallUtilities {

	private UnmarshallUtilities() {
	}

	public static Map<String, Object> unmarshal(StringBuilder sb) throws Exception {
		return JSON.std.mapFrom("{" + sb.toString() + "}");
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> unmarshalPrefixed(StringBuilder sb, String prefix)
			throws Exception {
		return (Map<String, Object>) unmarshal(sb).get(prefix);
	}

}

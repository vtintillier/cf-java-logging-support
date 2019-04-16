package com.sap.hcp.cf.logging.servlet.filter;

import static com.sap.hcp.cf.logging.common.RequestRecordBuilder.requestRecord;

import javax.servlet.http.HttpServletRequest;

import com.sap.hcp.cf.logging.common.*;

import java.util.List;

public class RequestRecordFactory {

	private final LogOptionalFieldsSettings logOptionalFieldsSettings;

	public RequestRecordFactory(LogOptionalFieldsSettings logOptionalFieldsSettings) {
		this.logOptionalFieldsSettings = logOptionalFieldsSettings;
	}

	public RequestRecord create(HttpServletRequest request) {
		boolean isSensitiveConnectionData = logOptionalFieldsSettings.isLogSensitiveConnectionData();
		boolean isLogRemoteUserField = logOptionalFieldsSettings.isLogRemoteUserField();
		boolean isLogRefererField = logOptionalFieldsSettings.isLogRefererField();
		RequestRecordBuilder rrb =  requestRecord("[SERVLET]").addTag(Fields.REQUEST, getFullRequestUri(request))
				.addTag(Fields.METHOD, request.getMethod())
				.addTag(Fields.PROTOCOL, getValue(request.getProtocol()))
				.addContextTag(Fields.REQUEST_ID, getHeader(request, HttpHeaders.X_VCAP_REQUEST_ID))
				.addOptionalTag(isSensitiveConnectionData, Fields.REMOTE_IP, getValue(request.getRemoteAddr()))
				.addOptionalTag(isSensitiveConnectionData, Fields.REMOTE_HOST, getValue(request.getRemoteHost()))
				.addOptionalTag(isSensitiveConnectionData, Fields.REMOTE_PORT,
						Integer.toString(request.getRemotePort()))
				.addOptionalTag(isSensitiveConnectionData, Fields.X_FORWARDED_FOR,
						getHeader(request, HttpHeaders.X_FORWARDED_FOR))
				.addOptionalTag(isLogRemoteUserField, Fields.REMOTE_USER, getValue(request.getRemoteUser()))
				.addOptionalTag(isLogRefererField, Fields.REFERER, getHeader(request, HttpHeaders.REFERER));
		for(String header: HttpHeaders.PROPAGATED_HEADERS) {
			rrb.addContextTag(LogContextAdapter.getField(header), getHeader(request, header));
		}
		return rrb.build();
	}

	private String getFullRequestUri(HttpServletRequest request) {
		String queryString = request.getQueryString();
		String requestURI = request.getRequestURI();
		return queryString != null ? requestURI + "?" + queryString : requestURI;
	}

	private String getHeader(HttpServletRequest request, String headerName) {
		List<String> headers = HttpHeaders.ALIASES.get(headerName);
		if (headers == null) {
			return getValue(request.getHeader(headerName));
		}
		for (String header: headers) {
			String value = request.getHeader(header);
			if (value != null) {
				return value;
			}
		}
		return Defaults.UNKNOWN;
	}

	private String getValue(String value) {
		return value != null ? value : Defaults.UNKNOWN;
	}

}

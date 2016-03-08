package com.sap.hcp.cf.logback.encoder;

import static com.sap.hcp.cf.logging.common.PatternUtils.JSON_FIELD;

import com.sap.hcp.cf.logging.common.Defaults;
import com.sap.hcp.cf.logging.common.Fields;

/**
 * Defines the "built-in" layout pattern for our JSON encoder.
 *
 */
public final class LayoutPatterns {
	
	public static enum PATTERN_KEY {
		APPLICATION,
		EXCEPTION,
		REQUEST
	}
	
	/*
	 * -- this is the common prefix to all variants.
	 * -- the final line will add non-predefined context parameters from the MDC
	 * -- as this list may be empty, we use "replace" to at a colon if it's not 
	 */
	private static final String COMMON_PREFIX_PATTERN = 
		"{ " +
		JSON_FIELD(Fields.WRITTEN_AT, "%d{yyyy-MM-dd'T'HH:mm:ss.SSSX,UTC}", true, true) +
		JSON_FIELD(Fields.WRITTEN_TS, "%tstamp", false, false) +
		"%replace(%ctxp){'(.+)', ',$1'},";
	
	/*
	 * -- all layout patterns end like this
	 */
	private static final String COMMON_POSTFIX_PATTERN = 
		"}%n";
		
	/*
	 * -- for standard application log messages we always add these fields.
	 * -- note the last line where we make sure that the original message string
	 * -- is quoted and properly escaped and that "custom fields" are added
	 */
	private static final String APP_PREFIX_PATTERN = 	
		JSON_FIELD(Fields.TYPE, Defaults.TYPE_LOG, true, true) +
		JSON_FIELD(Fields.LOGGER, "%logger", true, true) +
		JSON_FIELD(Fields.THREAD, "%thread", true, true) +
		JSON_FIELD(Fields.LEVEL, "%p", true, true) +
		JSON_FIELD(Fields.CATEGORIES, "%categories", false, true) +
		JSON_FIELD(Fields.MSG, "%jsonmsg{escape}%replace(%args{custom_fields}){'(.+)', ',$1'}", false, false);

	/*
	 * -- a simple application log message does not include exception/stack trace info
	 */
	private static final String APP_LOG_PATTERN = 
		   COMMON_PREFIX_PATTERN +
		   APP_PREFIX_PATTERN +
		   "%nopex " +
		   COMMON_POSTFIX_PATTERN;
	
	/*
	 * -- if the application log was written including stack trace info, write it out!
	 * -- to avoid any trouble with our event processing pipeline that expect a single
	 * -- line message, make sure newlines and tabs are escaped.
	 * -- newlines are actually escaped with additional surrounding spaces to make the
	 * -- standard ES tokenizer work   
	 */
	private static final String APP_EX_LOG_PATTERN =
			COMMON_PREFIX_PATTERN +
			APP_PREFIX_PATTERN +
			"," + JSON_FIELD(Fields.STACKTRACE, "%stacktrace", false, false) + "%nopex " +
			COMMON_POSTFIX_PATTERN;
	
	/*
	 * -- if we write a request log/"beat" all we want/need is already in the message (object), but
	 * -- we need to flatten it as we don't want nested JSON objects.
	 */
	private static final String REQUEST_METRICS_PATTERN =
			COMMON_PREFIX_PATTERN +
			JSON_FIELD(Fields.TYPE, Defaults.TYPE_REQUEST, true, true) +
			"%jsonmsg{flatten}%nopex" +
			COMMON_POSTFIX_PATTERN;
	
	public static String getPattern(PATTERN_KEY key) {
		switch (key) {		
		case REQUEST:
			return REQUEST_METRICS_PATTERN;
			
		case EXCEPTION:
			return APP_EX_LOG_PATTERN;
			
		case APPLICATION:
		default:
			return APP_LOG_PATTERN;
		}
	}
}

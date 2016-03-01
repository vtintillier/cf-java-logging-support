package com.sap.hcp.cf.logging.common;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class TestAppLog extends AbstractTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(TestAppLog.class);
	private String logMsg;
	
	@Test
	public void test() {
		logMsg = "Running test()";
		LOGGER.info(logMsg);
		assertThat(getMessage(), is(logMsg));
		assertThat(getField(Fields.COMPONENT_ID), is("-"));
		assertThat(getField(Fields.COMPONENT_NAME), is("-"));
		assertThat(getField(Fields.COMPONENT_INSTANCE), is("0"));
	}

	@Test
	public void testMDC() {
		MDC.put(SOME_KEY, SOME_VALUE);
		MDC.put("testNumeric", "200");
		logMsg = "Running testMDC()";
		LOGGER.info(logMsg);
		assertThat(getMessage(), is(logMsg));
		assertThat(getField(Fields.COMPONENT_ID), is("-"));
		assertThat(getField(Fields.COMPONENT_NAME), is("-"));
		assertThat(getField(Fields.COMPONENT_INSTANCE), is("0"));
	}

	@Test
	public void testStacktrace() {
		try {
			Double.parseDouble(null);
		}
		catch (Exception ex) {
			logMsg = "Running testStacktrace()";
			LOGGER.error(logMsg, ex);
			assertThat(getMessage(), is(logMsg));
			assertThat(getField(Fields.COMPONENT_ID), is("-"));
			assertThat(getField(Fields.COMPONENT_NAME), is("-"));
			assertThat(getField(Fields.COMPONENT_INSTANCE), is("0"));
			assertThat(getField(Fields.STACKTRACE), is(notNullValue())); 
		}
	}
	
	@Test
	public void testJSONMsg() {
		String jsonMsg = "{\"" + SOME_KEY + "\":\"" + SOME_VALUE + "\"}";
		LOGGER.info(jsonMsg);
		assertThat(getMessage(), is(jsonMsg));		
	}
}

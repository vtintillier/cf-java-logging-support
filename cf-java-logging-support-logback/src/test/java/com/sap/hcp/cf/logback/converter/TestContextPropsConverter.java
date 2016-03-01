package com.sap.hcp.cf.logback.converter;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.slf4j.MDC;

import com.fasterxml.jackson.jr.ob.JSONObjectException;
import com.sap.hcp.cf.logback.converter.ContextPropsConverter;

public class TestContextPropsConverter extends AbstractConverterTest {
	
	@Test
	public void testEmpty() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter();
		MDC.clear();
		cpc.start();
		Map<String, Object> actMap = mapFrom(cpc.convert(makeEvent(TEST_MSG_NO_ARGS, NO_ARGS)));
		assertThat(actMap, is(mdcMap()));
	}
	
	@Test
	public void testSingleArg() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter();
		cpc.start();
		MDC.clear();
		MDC.put(SOME_KEY, SOME_VALUE);
		Map<String, Object> actMap = mapFrom(cpc.convert(makeEvent(TEST_MSG_NO_ARGS, NO_ARGS)));
		assertThat(actMap, is(mdcMap()));		
	}
	
	@Test
	public void testTwoArgs() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter();
		cpc.start();
		MDC.clear();
		MDC.put(SOME_KEY, SOME_VALUE);
		MDC.put(SOME_OTHER_KEY, SOME_OTHER_VALUE);
		Map<String, Object> actMap = mapFrom(cpc.convert(makeEvent(TEST_MSG_NO_ARGS, NO_ARGS)));
		assertThat(actMap, is(mdcMap()));		
	}
	
	@Test
	public void testStrangeArgs() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter();
		cpc.start();
		MDC.clear();
		MDC.put(SOME_KEY, SOME_VALUE);
		MDC.put(STRANGE_SEQ, STRANGE_SEQ);
		Map<String, Object> actMap = mapFrom(cpc.convert(makeEvent(TEST_MSG_NO_ARGS, NO_ARGS)));
		assertThat(actMap, is(mdcMap()));	
	}

	@Test
	public void testExclusion() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter();
		cpc.setOptionList(asList(SOME_KEY));
		cpc.start();
		MDC.clear();
		MDC.put(SOME_KEY, SOME_VALUE);
		MDC.put(SOME_OTHER_KEY, SOME_OTHER_VALUE);
		Map<String, Object> actMap = mapFrom(cpc.convert(makeEvent(TEST_MSG_NO_ARGS, NO_ARGS)));
		assertThat(actMap, is(mdcMap(asList(SOME_KEY))));	
	}
	
	@Test
	public void testExclusionStrangeSeq() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter();
		cpc.setOptionList(asList(STRANGE_SEQ));
		cpc.start();
		MDC.clear();
		MDC.put(SOME_KEY, SOME_VALUE);
		MDC.put(STRANGE_SEQ, STRANGE_SEQ);
		Map<String, Object> actMap = mapFrom(cpc.convert(makeEvent(TEST_MSG_NO_ARGS, NO_ARGS)));
		assertThat(actMap, is(mdcMap(asList(STRANGE_SEQ))));	
	}
}

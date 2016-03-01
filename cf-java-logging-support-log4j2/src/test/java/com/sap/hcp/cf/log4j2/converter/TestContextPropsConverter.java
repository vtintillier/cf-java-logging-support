package com.sap.hcp.cf.log4j2.converter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.MDC;

import com.fasterxml.jackson.jr.ob.JSONObjectException;
import com.sap.hcp.cf.log4j2.converter.ContextPropsConverter;

public class TestContextPropsConverter extends AbstractConverterTest {
	
	@Test
	public void testEmpty() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter(new String[0]);
		MDC.clear();
		assertThat(mapFrom(format(cpc,makeEvent(TEST_MSG_NO_ARGS, NO_ARGS))), is(mdcMap()));
	}
	
	@Test
	public void testSingleArg() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter(new String[0]);
		MDC.clear();
		MDC.put(SOME_KEY, SOME_VALUE);
		assertThat(mapFrom(format(cpc,makeEvent(TEST_MSG_NO_ARGS, NO_ARGS))), is(mdcMap()));		
	}
	
	@Test
	public void testTwoArgs() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter(new String[0]);
		MDC.clear();
		MDC.put(SOME_KEY, SOME_VALUE);
		MDC.put(SOME_OTHER_KEY, SOME_OTHER_VALUE);
		assertThat(mapFrom(format(cpc,makeEvent(TEST_MSG_NO_ARGS, NO_ARGS))), is(mdcMap()));		
	}
	
	@Test
	public void testStrangeArgs() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter(new String[0]);
		MDC.clear();
		MDC.put(SOME_KEY, SOME_VALUE);
		MDC.put(STRANGE_SEQ, STRANGE_SEQ);
		assertThat(mapFrom(format(cpc,makeEvent(TEST_MSG_NO_ARGS, NO_ARGS))), is(mdcMap()));	
	}

	@Test
	public void testExclusion() throws JSONObjectException, IOException {
		String[] exclusions = new String[] {SOME_KEY};
		ContextPropsConverter cpc = new ContextPropsConverter(exclusions);
		MDC.clear();
		MDC.put(SOME_KEY, SOME_VALUE);
		MDC.put(SOME_OTHER_KEY, SOME_OTHER_VALUE);
		assertThat(mapFrom(format(cpc,makeEvent(TEST_MSG_NO_ARGS, NO_ARGS))), is(mdcMap(exclusions)));	
	}
	
	@Test
	public void testExclusionStrangeSeq() throws JSONObjectException, IOException {
		String[] exclusions = new String[] {STRANGE_SEQ};
		ContextPropsConverter cpc = new ContextPropsConverter(exclusions);
		MDC.clear();
		MDC.put(SOME_KEY, SOME_VALUE);
		MDC.put(STRANGE_SEQ, STRANGE_SEQ);
		assertThat(mapFrom(format(cpc,makeEvent(TEST_MSG_NO_ARGS, NO_ARGS))), is(mdcMap(exclusions)));	
	}
}

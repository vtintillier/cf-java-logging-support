package com.sap.hcp.cf.log4j2.converter;

import static com.sap.hcp.cf.logging.common.customfields.CustomField.customField;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.MDC;

import com.fasterxml.jackson.jr.ob.JSONObjectException;

public class TestContextPropsConverter extends AbstractConverterTest {

	private static final String[] EXCLUDE_SOME_KEY = new String[] { SOME_KEY };
	private static final String[] NO_EXCLUSIONS = new String[0];

	@Test
    public void testEmpty() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter(NO_EXCLUSIONS);
        MDC.clear();
        assertThat(mapFrom(format(cpc, makeEvent(TEST_MSG_NO_ARGS, NO_ARGS))), is(mdcMap()));
    }

    @Test
    public void testSingleArg() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter(NO_EXCLUSIONS);
        MDC.clear();
        MDC.put(SOME_KEY, SOME_VALUE);
        assertThat(mapFrom(format(cpc, makeEvent(TEST_MSG_NO_ARGS, NO_ARGS))), is(mdcMap()));
    }

    @Test
    public void testTwoArgs() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter(NO_EXCLUSIONS);
        MDC.clear();
        MDC.put(SOME_KEY, SOME_VALUE);
        MDC.put(SOME_OTHER_KEY, SOME_OTHER_VALUE);
        assertThat(mapFrom(format(cpc, makeEvent(TEST_MSG_NO_ARGS, NO_ARGS))), is(mdcMap()));
    }

    @Test
    public void testStrangeArgs() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter(NO_EXCLUSIONS);
        MDC.clear();
        MDC.put(SOME_KEY, SOME_VALUE);
        MDC.put(STRANGE_SEQ, STRANGE_SEQ);
        assertThat(mapFrom(format(cpc, makeEvent(TEST_MSG_NO_ARGS, NO_ARGS))), is(mdcMap()));
    }

    @Test
    public void testExclusion() throws JSONObjectException, IOException {
		ContextPropsConverter cpc = new ContextPropsConverter(EXCLUDE_SOME_KEY);
        MDC.clear();
        MDC.put(SOME_KEY, SOME_VALUE);
        MDC.put(SOME_OTHER_KEY, SOME_OTHER_VALUE);
		assertThat(mapFrom(format(cpc, makeEvent(TEST_MSG_NO_ARGS, NO_ARGS))), is(mdcMap(EXCLUDE_SOME_KEY)));
    }

    @Test
    public void testExclusionStrangeSeq() throws JSONObjectException, IOException {
        String[] exclusions = new String[] { STRANGE_SEQ };
        ContextPropsConverter cpc = new ContextPropsConverter(exclusions);
        MDC.clear();
        MDC.put(SOME_KEY, SOME_VALUE);
        MDC.put(STRANGE_SEQ, STRANGE_SEQ);
        assertThat(mapFrom(format(cpc, makeEvent(TEST_MSG_NO_ARGS, NO_ARGS))), is(mdcMap(exclusions)));
    }

	@Test
	public void testUnregisteredCustomField() throws Exception {
		ContextPropsConverter cpc = new ContextPropsConverter(NO_EXCLUSIONS);
		assertThat(mapFrom(format(cpc, makeEvent(TEST_MSG_NO_ARGS, customField(SOME_KEY, SOME_VALUE)))),
				hasEntry(SOME_KEY, SOME_VALUE));
	}

	@Test
	public void testRegisteredCustomField() throws Exception {
		ContextPropsConverter cpc = new ContextPropsConverter(EXCLUDE_SOME_KEY);
		assertThat(mapFrom(format(cpc, makeEvent(TEST_MSG_NO_ARGS, customField(SOME_KEY, SOME_VALUE)))),
				not(hasEntry(SOME_KEY, SOME_VALUE)));
	}

}

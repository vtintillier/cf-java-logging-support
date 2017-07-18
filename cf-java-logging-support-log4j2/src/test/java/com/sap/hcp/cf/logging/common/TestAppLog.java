package com.sap.hcp.cf.logging.common;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

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
        assertThat(getField(Fields.WRITTEN_TS), is(notNullValue()));
    }

    @Test
    public void testCategorties() {
        logMsg = "Running testCategories()";
        Marker cat0 = MarkerFactory.getMarker("cat0");

        LOGGER.info(cat0, logMsg);
        assertThat(getMessage(), is(logMsg));
        assertThat(getField(Fields.COMPONENT_ID), is("-"));
        assertThat(getField(Fields.COMPONENT_NAME), is("-"));
        assertThat(getField(Fields.COMPONENT_INSTANCE), is("0"));
        assertThat(getField(Fields.WRITTEN_TS), is(notNullValue()));
        assertThat(getList(Fields.CATEGORIES), contains(cat0.getName()));

        Marker cat1 = MarkerFactory.getMarker("cat1");
        cat1.add(cat0);

        LOGGER.info(cat1, logMsg);
        assertThat(getMessage(), is(logMsg));
        assertThat(getField(Fields.COMPONENT_ID), is("-"));
        assertThat(getField(Fields.COMPONENT_NAME), is("-"));
        assertThat(getField(Fields.COMPONENT_INSTANCE), is("0"));
        assertThat(getField(Fields.WRITTEN_TS), is(notNullValue()));
        assertThat(getList(Fields.CATEGORIES), contains(cat1.getName(), cat0.getName()));
    }

    @Test
    public void testMDC() {
        MDC.put(SOME_KEY, SOME_VALUE);
        MDC.put("testNumeric", "200");
        logMsg = "Running testMDC()";
        long beforeTS = System.nanoTime();
        LOGGER.info(logMsg);
        long afterTS = System.nanoTime();
        assertThat(getMessage(), is(logMsg));
        assertThat(getField(Fields.COMPONENT_ID), is("-"));
        assertThat(getField(Fields.COMPONENT_NAME), is("-"));
        assertThat(getField(Fields.COMPONENT_INSTANCE), is("0"));
        assertThat(getField(Fields.WRITTEN_TS), is(notNullValue()));
        assertThat(getField(Fields.WRITTEN_TS), greaterThanOrEqualTo(Long.toString(beforeTS)));
        assertThat(Long.toString(afterTS), greaterThanOrEqualTo(getField(Fields.WRITTEN_TS)));
    }

    @Test
    public void testStacktrace() {
        try {
            Double.parseDouble(null);
        } catch (Exception ex) {
            logMsg = "Running testStacktrace()";
            LOGGER.error(logMsg, ex);
            assertThat(getMessage(), is(logMsg));
            assertThat(getField(Fields.COMPONENT_ID), is("-"));
            assertThat(getField(Fields.COMPONENT_NAME), is("-"));
            assertThat(getField(Fields.COMPONENT_INSTANCE), is("0"));
            assertThat(getField(Fields.STACKTRACE), is(notNullValue()));
            assertThat(getField(Fields.WRITTEN_TS), is(notNullValue()));
        }
    }

    @Test
    public void testJSONMsg() {
        String jsonMsg = "{\"" + SOME_KEY + "\":\"" + SOME_VALUE + "\"}";
        LOGGER.info(jsonMsg);
        assertThat(getMessage(), is(jsonMsg));
    }
}

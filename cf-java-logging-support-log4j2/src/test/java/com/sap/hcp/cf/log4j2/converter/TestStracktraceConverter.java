package com.sap.hcp.cf.log4j2.converter;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestStracktraceConverter extends AbstractConverterTest {

    private static final String[] NO_ARGS = new String[0];

    @Test
    public void testEmpty() {
        StacktraceConverter stc = new StacktraceConverter(NO_ARGS);
        assertThat(format(stc, makeEvent(null, null, null)), is(EMPTY));
    }

    @Test
    public void testSynthetic() throws Exception {
        StacktraceConverter stc = new StacktraceConverter(NO_ARGS);
        String actual = format(stc, makeEvent(null, new NullPointerException(), null));
        assertThat(actual, not(is(EMPTY)));
        assertThat(arrayElem(actual, 0).toString(), is(NullPointerException.class.getName()));
    }

    @SuppressWarnings("null")
    @Test
    public void testReal() throws Exception {
        StacktraceConverter stc = new StacktraceConverter(NO_ARGS);
        double a = 1.0, b = 0.0;
        try {
            /* -- force exception -- */
            new Double(a / b);
        } catch (Exception ex) {
            String actual = format(stc, makeEvent(null, ex, null));
            int stackDepth = ex.getStackTrace().length + 1;
            assertThat(actual, not(is(EMPTY)));
            assertThat(arrayElem(actual, 0).toString(), is(ex.getClass().getName()));
            assertThat(arrayFrom(actual).length, is(lessThanOrEqualTo(stackDepth)));

        }
    }
}

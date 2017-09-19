package com.sap.hcp.cf.log4j2.converter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.fasterxml.jackson.jr.ob.JSONObjectException;
import com.sap.hcp.cf.logging.common.helper.StacktraceGenerator;
import com.sap.hcp.cf.logging.common.helper.SubstringCounter;

public class TestStracktraceConverter extends AbstractConverterTest {
    private static final String[] NO_STRING_ARGS = new String[0];

    @Test
    public void testEmpty() {
        Log4JStacktraceConverter stc = new Log4JStacktraceConverter(NO_STRING_ARGS);
        assertThat(format(stc, makeEvent(null, null, null)), isEmptyString());
    }

    @Test
    public void testSynthetic() throws Exception {
        Log4JStacktraceConverter stc = new Log4JStacktraceConverter(NO_STRING_ARGS);
        String actual = format(stc, makeEvent(null, new NullPointerException(), null));
        assertThat(actual, not(isEmptyString()));
        assertThat(arrayElem(actual, 0).toString(), equalTo(NullPointerException.class.getName()));
    }

    @Test
    public void testReal() throws Exception {
        Log4JStacktraceConverter stc = new Log4JStacktraceConverter(NO_STRING_ARGS);
        double a = 1.0, b = 0.0;
        try {
            /* -- force exception -- */
            new Double(a / b);
        } catch (Exception ex) {
            String actual = format(stc, makeEvent(null, ex, null));
            int stackDepth = ex.getStackTrace().length + 1;
            assertThat(actual, not(isEmptyString()));
            assertThat(arrayElem(actual, 0).toString(), equalTo(ex.getClass().getName()));
            assertThat(arrayFrom(actual).length, lessThanOrEqualTo(stackDepth));
        }
    }

    /**
     * We create a long stack trace by calling functions recursively in the
     * Class StacktraceGenerator. The f1 and f3 parts are considered important,
     * whereas we're less interested in f2. The recursion depths are
     * hand-crafted according to DefaultStacktraceConverter.MAX_SIZE so that:
     * The total size of the stack trace exceeds MAX_SIZE; thus truncation
     * occurs. All the f3 lines are within the first MAX_SIZE/3 chars. All the
     * f1 lines are within the last MAX_SIZE/3*2 chars. The test succeeds if the
     * truncated stack trace contains all the f1 and f3 lines.
     */

    @Test
    public void testHuge() throws Exception {
        StacktraceGenerator stacktraceGenerator = new StacktraceGenerator(245, 330, 115);
        Exception ex = stacktraceGenerator.generateException();
        Log4JStacktraceConverter stc = new Log4JStacktraceConverter(NO_STRING_ARGS);
        String actual = format(stc, makeEvent(null, ex, null));

        // Did this return anything?
        assertThat(actual, not(isEmptyString()));
        // Was the stack trace really truncated?
        assertThat(getStacktraceLine(actual, 0), containsString("STACK TRACE TRUNCATED"));
        // Do we see the exception (in the line after "STACK TRACE
        // TRUNCATED")?
        assertThat(getStacktraceLine(actual, 1), containsString(ex.getClass().getName()));

        SubstringCounter substringCounter = new SubstringCounter();

        // Tests that all f1-functions which are considered important are
        // present in the stacktrace
        String f1 = "StacktraceGenerator.f1IsASimpleFunctionWithAnExceptionallyLongName";
        int expectedOccurrencesOf_f1 = stacktraceGenerator.getF1RecursionDepth() + 1;
        int countedOccurrencesOf_f1 = substringCounter.countOccurrencesOfSubstringInBigString(f1, actual);
        assertEquals(expectedOccurrencesOf_f1, countedOccurrencesOf_f1);

        // Tests that some f2-functions which represent the middle-part of the
        // stacktrace are missing
        String f2 = "StacktraceGenerator.f2IsASimpleFunctionWithAnExceptionallyLongName";
        int expectedOccurrencesOf_f2 = stacktraceGenerator.getF2RecursionDepth() + 1;
        int countedOccurrencesOf_f2 = substringCounter.countOccurrencesOfSubstringInBigString(f2, actual);
        assertThat(expectedOccurrencesOf_f2, Matchers.greaterThan(countedOccurrencesOf_f2));

        // Tests that all f3-functions which are considered important are
        // present in the stacktrace
        String f3 = "StacktraceGenerator.f3IsASimpleFunctionWithAnExceptionallyLongName";
        int expectedOccurrencesOf_f3 = stacktraceGenerator.getF3RecursionDepth() + 1;
        int countedOccurrencesOf_f3 = substringCounter.countOccurrencesOfSubstringInBigString(f3, actual);
        assertEquals(expectedOccurrencesOf_f3, countedOccurrencesOf_f3);
    }

    private String getStacktraceLine(String stacktrace, int lineNumber) throws JSONObjectException, IOException {
        return arrayElem(stacktrace, lineNumber).toString();
    }
}

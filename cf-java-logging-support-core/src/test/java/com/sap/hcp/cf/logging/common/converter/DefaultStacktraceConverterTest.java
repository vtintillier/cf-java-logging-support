package com.sap.hcp.cf.logging.common.converter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Test;

import com.sap.hcp.cf.logging.common.helper.StacktraceGenerator;

public class DefaultStacktraceConverterTest extends AbstractConverterTest {

    private String expectedString;
    StringBuilder stringBuilder = new StringBuilder();

    @Test
    public void testNull() {
        DefaultStacktraceConverter dstc = new DefaultStacktraceConverter();
        assertThat(formatStacktrace(dstc, null), is(EMPTY));
    }

    @Test
    public void testSynthetic() throws Exception {
        DefaultStacktraceConverter dstc = new DefaultStacktraceConverter();
        String actual = formatStacktrace(dstc, new NullPointerException());
        assertThat(actual, not(is(EMPTY)));
        assertThat(arrayElem(actual, 0).toString(), is(NullPointerException.class.getName()));
    }

    @Test
    public void testReal() throws Exception {
        DefaultStacktraceConverter dstc = new DefaultStacktraceConverter();

        try {
            throw new ArrayIndexOutOfBoundsException();
        } catch (ArrayIndexOutOfBoundsException ex) {
            String actual = formatStacktrace(dstc, ex);
            assertThat(actual, not(is(EMPTY)));
            assertThat(arrayElem(actual, 0).toString(), containsString(ex.getClass().getName()));
        }
    }

    @Test
    public void compareCompleteStacktraceWithExpectation() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                DefaultStacktraceConverter converter = new DefaultStacktraceConverter(4000);
                StacktraceGenerator generator = new StacktraceGenerator(5, 10, 5);
                converter.convert(generator.generateException(), stringBuilder);
            }
        });
        thread.start();
        thread.join();
        expectedString = "[\"java.lang.IllegalArgumentException: too long\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f3IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f3IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f3IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f3IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f3IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f3IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f2IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f2IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f2IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f2IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f2IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f2IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f2IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f2IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f2IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f2IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f2IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f1IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f1IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f1IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f1IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f1IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f1IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.generateException(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.converter.DefaultStacktraceConverterTest$X.run(DefaultStacktraceConverterTest.java:X)\"," + //
                         "\"\\tat java.lang.Thread.run(Thread.java:X)\"]";
        Assert.assertEquals(expectedString, stringBuilder.toString().replaceAll(":\\d+\\)", ":X)").//
                                                         replaceAll("\\$\\d", "\\$X"));
    }

    @Test
    public void compareTruncatedStacktraceWithExpectation() throws InterruptedException {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                DefaultStacktraceConverter converter = new DefaultStacktraceConverter(2000);
                StacktraceGenerator generator = new StacktraceGenerator(5, 20, 5);
                converter.convert(generator.generateException(), stringBuilder);
            }
        });
        thread.start();
        thread.join();
        expectedString = "[\"-------- STACK TRACE TRUNCATED --------\"," + //
                         "\"java.lang.IllegalArgumentException: too long\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f3IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f3IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f3IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f3IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"-------- OMITTED --------\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f2IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f1IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f1IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f1IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f1IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f1IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.f1IsASimpleFunctionWithAnExceptionallyLongName(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.helper.StacktraceGenerator.generateException(StacktraceGenerator.java:X)\"," + //
                         "\"\\tat com.sap.hcp.cf.logging.common.converter.DefaultStacktraceConverterTest$X.run(DefaultStacktraceConverterTest.java:X)\"," + //
                         "\"\\tat java.lang.Thread.run(Thread.java:X)\"]";
        Assert.assertEquals(expectedString, stringBuilder.toString().replaceAll(":\\d+\\)", ":X)").//
                                                         replaceAll("\\$\\d", "\\$X"));
    }

}

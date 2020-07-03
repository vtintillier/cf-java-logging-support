package com.sap.hcp.cf.logging.common.converter;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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
				"\"\\tat " + getThreadClassName() + ".run(Thread.java:X)\"]";
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
				"\"\\tat " + getThreadClassName() + ".run(Thread.java:X)\"]";
        Assert.assertEquals(expectedString, stringBuilder.toString().replaceAll(":\\d+\\)", ":X)").//
                                                         replaceAll("\\$\\d", "\\$X"));
    }

	// This method is required to account for the module path in stacktraces
	// after Java 9
	private String getThreadClassName() {
		String stackTraceElement = Thread.currentThread().getStackTrace()[0].toString();
		int i = stackTraceElement.indexOf("Thread");
		return stackTraceElement.subSequence(0, i) + Thread.class.getSimpleName();
	}

	/**
	 * This test case can be used for failure analysis. Paste a custom stacktrace
	 * into src/test/resources/com/sap/hcp/cf/logging/converter/stacktrace.txt and
	 * unignore this test. It will print the formatted stacktrace to the console.
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore("Use this test for failure analysis of custom stacktraces.")
	public void customStacktrace() throws Exception {
		try (InputStream input = getClass().getResourceAsStream("stacktrace.txt");
				Reader reader = new InputStreamReader(input);
				BufferedReader stacktrace = new BufferedReader(reader)) {
			Throwable exception = Mockito.mock(Throwable.class);
			Mockito.when(exception.getMessage()).thenReturn("Test-Message");
			Mockito.doAnswer(new Answer<Void>() {

				@Override
				public Void answer(InvocationOnMock invocation) throws Throwable {
					PrintWriter writer = (PrintWriter) invocation.getArguments()[0];
					stacktrace.lines().forEach(l -> writer.println(l));
					return null;
				}
			}).when(exception).printStackTrace(Matchers.any(PrintWriter.class));
			DefaultStacktraceConverter converter = new DefaultStacktraceConverter();
			StringBuilder sb = new StringBuilder();
			converter.convert(exception, sb);
			System.out.print(sb.toString());
		}
	}

}

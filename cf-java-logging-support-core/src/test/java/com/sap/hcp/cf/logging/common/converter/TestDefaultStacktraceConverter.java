package com.sap.hcp.cf.logging.common.converter;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

import org.junit.Test;

import com.sap.hcp.cf.logging.common.converter.DefaultStacktraceConverter;

public class TestDefaultStacktraceConverter extends AbstractConverterTest {

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
		double a = 1.0, b = 0.0;
		try {
			/* -- force exception -- */
			new Double(a/b);
		}
		catch (Exception ex) {
			String actual = formatStacktrace(dstc, ex);
			assertThat(actual, not(is(EMPTY)));
			assertThat(arrayElem(actual, 0).toString(), is(ex.getClass().getName()));
		}
	}
}

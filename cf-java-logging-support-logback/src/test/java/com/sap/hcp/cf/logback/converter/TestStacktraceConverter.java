package com.sap.hcp.cf.logback.converter;

import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sap.hcp.cf.logback.converter.StacktraceConverter;

public class TestStacktraceConverter extends AbstractConverterTest {

	@Test
	public void testEmpty() {
		StacktraceConverter stc = new StacktraceConverter();
		stc.start();
		assertThat(stc.convert(makeEvent(null, null, null)), is(nullValue()));
	}
	
	@Test
	public void testSynthetic() throws Exception {
		StacktraceConverter stc = new StacktraceConverter();
		stc.start();
		String actual = stc.convert(makeEvent(null, new NullPointerException(), null));
		assertThat(actual, not(is(EMPTY)));
		assertThat(arrayElem(actual, 0).toString(), is(NullPointerException.class.getName()));	
	}
	
	@SuppressWarnings("null")
	@Test
	public void testReal() throws Exception {
		StacktraceConverter stc = new StacktraceConverter();
		stc.start();
		double a = 1.0, b = 0.0;
		try {
			/* -- force exception -- */
			new Double(a/b);
		}
		catch (Exception ex) {
			String actual = stc.convert(makeEvent(null, ex, null));
			int stackDepth = ex.getStackTrace().length + 1;
			assertThat(actual, not(is(EMPTY)));
			assertThat(arrayElem(actual, 0).toString(), is(ex.getClass().getName()));
			assertThat(arrayFrom(actual).length, is(lessThanOrEqualTo(stackDepth)));
			
		}
	}

}

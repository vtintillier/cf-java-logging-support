package com.sap.hcp.cf.log4j2.converter;

import static com.sap.hcp.cf.logging.common.customfields.CustomField.customField;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.jr.ob.JSONObjectException;
import com.sap.hcp.cf.log4j2.converter.ArgsConverter;
import com.sap.hcp.cf.logging.common.customfields.CustomField;

public class TestArgsConverter extends AbstractConverterTest {
	
	@Test
	public void testEmbeddedEmpty() {
		ArgsConverter ac = new ArgsConverter(null);

		assertThat(format(ac, makeEvent(TEST_MSG_NO_ARGS, NO_ARGS)), is(EMPTY));
	}
	
	@Test
	public void testNonEmbeddedEmpty() {
		ArgsConverter ac = new ArgsConverter(new String[] {PREFIX});

		assertThat(format(ac, makeEvent(TEST_MSG_NO_ARGS, NO_ARGS)), is(EMPTY));
	}

	@Test
	public void testEmbeddedSingleArg() throws JSONObjectException, IOException {
		ArgsConverter ac = new ArgsConverter(null);
		CustomField[] custFields = new CustomField[] {customField(SOME_KEY, SOME_VALUE)};
		
		assertThat(makeMap(custFields), is(mapFrom(format(ac, makeEvent(TEST_MSG_NO_ARGS, custFields)))));		
	}
	
	@Test
	public void testNonEmbeddedSingleArg() throws JSONObjectException, IOException {
		ArgsConverter ac = new ArgsConverter(new String[] {PREFIX});
		CustomField[] custFields = new CustomField[] {customField(SOME_KEY, SOME_VALUE)};
		
		assertThat(makeMap(custFields), is(mapFrom(format(ac, makeEvent(TEST_MSG_NO_ARGS, custFields))).get(PREFIX)));		
	}

	@Test
	public void testEmbeddedStrangeValue() throws JSONObjectException, IOException {
		ArgsConverter ac = new ArgsConverter(null);
		CustomField[] custFields = new CustomField[] {customField(SOME_KEY, STRANGE_SEQ)};
		
		assertThat(makeMap(custFields), is(mapFrom(format(ac, makeEvent(TEST_MSG_NO_ARGS, custFields)))));		
	}
	
	@Test
	public void testNonEmbeddedStrangeValue() throws JSONObjectException, IOException {
		ArgsConverter ac = new ArgsConverter(new String[] {PREFIX});
		CustomField[] custFields = new CustomField[] {customField(SOME_KEY, STRANGE_SEQ)};
		
		assertThat(makeMap(custFields),  is(mapFrom(format(ac, makeEvent(TEST_MSG_NO_ARGS, custFields))).get(PREFIX)));		
	}
	
	@Test
	public void testEmbeddedStrangeKey() throws JSONObjectException, IOException {
		ArgsConverter ac = new ArgsConverter(null);
		CustomField[] custFields = new CustomField[] {customField(STRANGE_SEQ, STRANGE_SEQ)};
		
		assertThat(makeMap(custFields), is(mapFrom(format(ac, makeEvent(TEST_MSG_NO_ARGS, custFields)))));		
	}
	
	@Test
	public void testNonEmbeddedStrangeKey() throws JSONObjectException, IOException {
		ArgsConverter ac = new ArgsConverter(new String[] {PREFIX});
		CustomField[] custFields = new CustomField[] {customField(STRANGE_SEQ, STRANGE_SEQ)};
		
		assertThat(makeMap(custFields), is(mapFrom(format(ac, makeEvent(TEST_MSG_NO_ARGS, custFields))).get(PREFIX)));		
	}
	
	@Test
	public void testNonEmbeddedStrangeFieldName() throws JSONObjectException, IOException {
		ArgsConverter ac = new ArgsConverter(new String[] {STRANGE_SEQ});
		CustomField[] custFields = new CustomField[] {customField(STRANGE_SEQ, STRANGE_SEQ)};
		
		assertThat(makeMap(custFields), is(mapFrom(format(ac, makeEvent(TEST_MSG_NO_ARGS, custFields))).get(STRANGE_SEQ)));		
	}
}

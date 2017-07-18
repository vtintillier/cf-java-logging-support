package com.sap.hcp.cf.logging.common.converter;

import static com.sap.hcp.cf.logging.common.customfields.CustomField.customField;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.jr.ob.JSONObjectException;
import com.sap.hcp.cf.logging.common.customfields.CustomField;

public class TestDefaultArgsConverter extends AbstractConverterTest {

    @Test
    public void testEmbeddedEmpty() {
        DefaultArgsConverter ac = new DefaultArgsConverter();

        assertThat(formatArgs(ac, NO_ARGS), is(EMPTY));
    }

    @Test
    public void testNonEmbeddedEmpty() {
        DefaultArgsConverter ac = new DefaultArgsConverter();
        ac.setFieldName(PREFIX);

        assertThat(formatArgs(ac, NO_ARGS), is(EMPTY));
    }

    @Test
    public void testStandardField() {
        DefaultArgsConverter ac = new DefaultArgsConverter();

        assertThat(formatArgs(ac, NON_CUSTOM_ARGS), is(EMPTY));
    }

    @Test
    public void testEmbeddedSingleArg() throws JSONObjectException, IOException {
        DefaultArgsConverter ac = new DefaultArgsConverter();
        CustomField[] custFields = new CustomField[] { customField(SOME_KEY, SOME_VALUE) };

        assertThat(makeMap(custFields), is(mapFrom(formatArgs(ac, custFields))));
    }

    @Test
    public void testNonEmbeddedSingleArg() throws JSONObjectException, IOException {
        DefaultArgsConverter ac = new DefaultArgsConverter();
        ac.setFieldName(PREFIX);
        CustomField[] custFields = new CustomField[] { customField(SOME_KEY, SOME_VALUE) };

        assertThat(makeMap(custFields), is(mapFrom(formatArgs(ac, custFields)).get(PREFIX)));
    }

    @Test
    public void testEmbeddedStrangeValue() throws JSONObjectException, IOException {
        DefaultArgsConverter ac = new DefaultArgsConverter();
        CustomField[] custFields = new CustomField[] { customField(SOME_KEY, STRANGE_SEQ) };

        assertThat(makeMap(custFields), is(mapFrom(formatArgs(ac, custFields))));
    }

    @Test
    public void testNonEmbeddedStrangeValue() throws JSONObjectException, IOException {
        DefaultArgsConverter ac = new DefaultArgsConverter();
        ac.setFieldName(PREFIX);
        CustomField[] custFields = new CustomField[] { customField(SOME_KEY, STRANGE_SEQ) };

        assertThat(makeMap(custFields), is(mapFrom(formatArgs(ac, custFields)).get(PREFIX)));
    }

    @Test
    public void testEmbeddedStrangeKey() throws JSONObjectException, IOException {
        DefaultArgsConverter ac = new DefaultArgsConverter();
        CustomField[] custFields = new CustomField[] { customField(STRANGE_SEQ, STRANGE_SEQ) };

        assertThat(makeMap(custFields), is(mapFrom(formatArgs(ac, custFields))));
    }

    @Test
    public void testNonEmbeddedStrangeKey() throws JSONObjectException, IOException {
        DefaultArgsConverter ac = new DefaultArgsConverter();
        ac.setFieldName(PREFIX);
        CustomField[] custFields = new CustomField[] { customField(STRANGE_SEQ, STRANGE_SEQ) };

        assertThat(makeMap(custFields), is(mapFrom(formatArgs(ac, custFields)).get(PREFIX)));
    }

    @Test
    public void testNonEmbeddedStrangeFieldName() throws JSONObjectException, IOException {
        DefaultArgsConverter ac = new DefaultArgsConverter();
        ac.setFieldName(STRANGE_SEQ);
        CustomField[] custFields = new CustomField[] { customField(STRANGE_SEQ, STRANGE_SEQ) };

        assertThat(makeMap(custFields), is(mapFrom(formatArgs(ac, custFields)).get(STRANGE_SEQ)));
    }

}

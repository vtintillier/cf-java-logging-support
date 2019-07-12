package com.sap.hcp.cf.logging.common.converter;

import static com.sap.hcp.cf.logging.common.converter.UnmarshallUtilities.unmarshal;
import static com.sap.hcp.cf.logging.common.converter.UnmarshallUtilities.unmarshalPrefixed;
import static com.sap.hcp.cf.logging.common.customfields.CustomField.customField;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class DefaultCustomFieldsConverterTest {

	private static final String HACK_ATTEMPT = "}{:\",\"";
	private DefaultCustomFieldsConverter converter;

	@Before
	public void initConverter() {
		this.converter = new DefaultCustomFieldsConverter();
	}

	@Test
	public void emptyMdcAndArguments() {
		StringBuilder sb = new StringBuilder();

		converter.convert(sb, Collections.emptyMap());

		assertThat(sb, hasToString(""));
	}

	@Test
	public void standardArgument() throws Exception {
		StringBuilder sb = new StringBuilder();
		converter.convert(sb, Collections.emptyMap(), "an_argument");
		assertThat(sb, hasToString(""));
	}

	@Test
	public void singleCustomFieldArgumentEmbedded() throws Exception {
		StringBuilder sb = new StringBuilder();

		converter.convert(sb, Collections.emptyMap(), customField("some key", "some value"));

		assertThat(unmarshal(sb), hasEntry("some key", "some value"));
	}

	@Test
	public void singleCustomFieldArgumentPrefix() throws Exception {
		converter.setFieldName("prefix");
		StringBuilder sb = new StringBuilder();

		converter.convert(sb, Collections.emptyMap(), customField("some key", "some value"));

		assertThat(unmarshalPrefixed(sb, "prefix"), hasEntry("some key", "some value"));
	}

	@Test
	public void singleMdcField() throws Exception {
		StringBuilder sb = new StringBuilder();
		
		@SuppressWarnings("serial")
		Map<String, String> mdcFields = new HashMap<String, String>() {
			{
	        put("some key", "some value");
	    }};

		converter.convert(sb, mdcFields);

		assertThat(unmarshal(sb), hasEntry("some key", "some value"));
	}

	@Test
	public void mergesMdcFieldsAndArguments() throws Exception {
		StringBuilder sb = new StringBuilder();

		@SuppressWarnings("serial")
		Map<String, String> mdcFields = new HashMap<String, String>() {
			{
				put("mdc key", "mdc value");
			}
		};

		converter.convert(sb, mdcFields, customField("some key", "some value"));

		assertThat(unmarshal(sb),
				allOf(hasEntry("some key", "some value"), hasEntry("mdc key", "mdc value")));
	}

	@Test
	public void properlyEscapesValues() throws Exception {
		StringBuilder sb = new StringBuilder();

		converter.convert(sb, Collections.emptyMap(), customField("some key", HACK_ATTEMPT));

		assertThat(unmarshal(sb), hasEntry("some key", HACK_ATTEMPT));
	}

	@Test
	public void properlyEscapesKeys() throws Exception {
		StringBuilder sb = new StringBuilder();

		converter.convert(sb, Collections.emptyMap(), customField(HACK_ATTEMPT, "some value"));

		assertThat(unmarshal(sb), hasEntry(HACK_ATTEMPT, "some value"));
	}

	@Test
	public void properlyEscapesMdcFields() throws Exception {
		StringBuilder sb = new StringBuilder();
		
		@SuppressWarnings("serial")
		Map<String, String> mdcFields = new HashMap<String, String>() {
			{
				put(HACK_ATTEMPT, HACK_ATTEMPT);
	    }};

		converter.convert(sb, mdcFields);

		assertThat(unmarshal(sb), hasEntry(HACK_ATTEMPT, HACK_ATTEMPT));

	}

	@Test
	public void properlyEscapesFieldNames() throws Exception {
		converter.setFieldName(HACK_ATTEMPT);
		StringBuilder sb = new StringBuilder();

		converter.convert(sb, Collections.emptyMap(), customField("some key", "some value"));

		assertThat(unmarshalPrefixed(sb, HACK_ATTEMPT), hasEntry("some key", "some value"));
	}

}


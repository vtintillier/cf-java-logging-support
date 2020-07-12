package com.sap.hcp.cf.logging.common.converter;

import static com.sap.hcp.cf.logging.common.converter.CustomFieldMatchers.hasCustomField;
import static com.sap.hcp.cf.logging.common.converter.UnmarshallUtilities.unmarshalCustomFields;
import static com.sap.hcp.cf.logging.common.customfields.CustomField.customField;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class DefaultCustomFieldsConverterTest {

	private static final String CUSTOM_KEY_0 = "custom_key_0";
	private static final String CUSTOM_VALUE_0 = "custom_value_0";
	private static final String CUSTOM_KEY_1 = "custom_key_1";
	private static final String CUSTOM_VALUE_1 = "custom_value_1";
	private static final String UNREGISTERED_KEY = "unregistered_key";
	private static final String UNREGISTERED_VALUE = "unregistered_value";
	private static final String HACK_ATTEMPT = "}{:\",\"";
	private DefaultCustomFieldsConverter converter;

	@Before
	public void initConverter() {
		this.converter = new DefaultCustomFieldsConverter();
		converter.setCustomFieldKeyNames(Arrays.asList(CUSTOM_KEY_0, CUSTOM_KEY_1));
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

		converter.convert(sb, Collections.emptyMap(), customField(CUSTOM_KEY_0, CUSTOM_VALUE_0));

		assertThat(unmarshalCustomFields(sb), contains(hasCustomField(CUSTOM_KEY_0, CUSTOM_VALUE_0, 0)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void multipleCustomFieldArgumentEmbedded() throws Exception {
		StringBuilder sb = new StringBuilder();

		converter.convert(sb, Collections.emptyMap(), customField(CUSTOM_KEY_1, CUSTOM_VALUE_1),
				customField(UNREGISTERED_KEY, UNREGISTERED_VALUE), customField(CUSTOM_KEY_0, CUSTOM_VALUE_0));

		assertThat(unmarshalCustomFields(sb), containsInAnyOrder(hasCustomField(CUSTOM_KEY_0, CUSTOM_VALUE_0, 0),
				hasCustomField(CUSTOM_KEY_1, CUSTOM_VALUE_1, 1)));
	}

	@Test
	public void singleCustomFieldArgumentPrefix() throws Exception {
		converter.setFieldName("prefix");
		StringBuilder sb = new StringBuilder();

		converter.convert(sb, Collections.emptyMap(), customField(CUSTOM_KEY_0, CUSTOM_VALUE_0));

		assertThat(unmarshalCustomFields(sb, "prefix"), contains(hasCustomField(CUSTOM_KEY_0, CUSTOM_VALUE_0, 0)));
	}

	@Test
	public void singleMdcField() throws Exception {
		StringBuilder sb = new StringBuilder();

		@SuppressWarnings("serial")
		Map<String, String> mdcFields = new HashMap<String, String>() {
			{
				put(CUSTOM_KEY_0, CUSTOM_VALUE_0);
			}
		};

		converter.convert(sb, mdcFields);

		assertThat(unmarshalCustomFields(sb), contains(hasCustomField(CUSTOM_KEY_0, CUSTOM_VALUE_0, 0)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void multipleMdcFields() throws Exception {
		StringBuilder sb = new StringBuilder();

		@SuppressWarnings("serial")
		Map<String, String> mdcFields = new HashMap<String, String>() {
			{
				put(CUSTOM_KEY_1, CUSTOM_VALUE_1);
				put(UNREGISTERED_KEY, UNREGISTERED_VALUE);
				put(CUSTOM_KEY_0, CUSTOM_VALUE_0);
			}
		};

		converter.convert(sb, mdcFields);

		assertThat(unmarshalCustomFields(sb), containsInAnyOrder(hasCustomField(CUSTOM_KEY_0, CUSTOM_VALUE_0, 0),
				hasCustomField(CUSTOM_KEY_1, CUSTOM_VALUE_1, 1)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void argumentsTakePrecendenceOverMdc() throws Exception {
		StringBuilder sb = new StringBuilder();

		@SuppressWarnings("serial")
		Map<String, String> mdcFields = new HashMap<String, String>() {
			{
				put(CUSTOM_KEY_0, CUSTOM_VALUE_0);
				put(CUSTOM_KEY_1, CUSTOM_VALUE_1);
			}
		};

		converter.convert(sb, mdcFields, customField(CUSTOM_KEY_0, "preferred value"));

		assertThat(unmarshalCustomFields(sb), containsInAnyOrder(hasCustomField(CUSTOM_KEY_0, "preferred value", 0),
				hasCustomField(CUSTOM_KEY_1, CUSTOM_VALUE_1, 1)));
	}

	@Test
	public void doesNotWriteJsonWhenNoFieldKeysAreConfigured() throws Exception {
		StringBuilder sb = new StringBuilder();

		converter.setCustomFieldKeyNames(Collections.emptyList());
		converter.convert(sb, Collections.emptyMap(), customField(CUSTOM_KEY_0, CUSTOM_VALUE_0));

		assertThat(sb.toString(), isEmptyString());
	}

	@Test
	public void properlyEscapesValues() throws Exception {
		StringBuilder sb = new StringBuilder();

		converter.convert(sb, Collections.emptyMap(), customField(CUSTOM_KEY_0, HACK_ATTEMPT));

		assertThat(unmarshalCustomFields(sb), contains(hasCustomField(CUSTOM_KEY_0, HACK_ATTEMPT, 0)));
	}

	@Test
	public void properlyEscapesMdcFields() throws Exception {
		StringBuilder sb = new StringBuilder();

		@SuppressWarnings("serial")
		Map<String, String> mdcFields = new HashMap<String, String>() {
			{
				put(CUSTOM_KEY_0, HACK_ATTEMPT);
			}
		};

		converter.convert(sb, mdcFields);

		assertThat(unmarshalCustomFields(sb), contains(hasCustomField(CUSTOM_KEY_0, HACK_ATTEMPT, 0)));

	}

	@Test
	public void properlyEscapesFieldNames() throws Exception {
		converter.setFieldName(HACK_ATTEMPT);
		StringBuilder sb = new StringBuilder();

		converter.convert(sb, Collections.emptyMap(), customField(CUSTOM_KEY_0, CUSTOM_VALUE_0));

		assertThat(unmarshalCustomFields(sb, HACK_ATTEMPT),
				contains(hasCustomField(CUSTOM_KEY_0, CUSTOM_VALUE_0, 0)));
	}

}

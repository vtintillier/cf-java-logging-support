package com.sap.hcp.cf.log4j2.layout;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sap.hcp.cf.logging.common.LogContext;

public class CustomFieldsAdapterTest {

	@Test
	public void returnsEmptyCustomFieldsWithoutConfig() throws Exception {
		CustomFieldsAdapter adapter = new CustomFieldsAdapter();

		assertThat(adapter.getCustomFieldKeyNames(), is(empty()));
	}
	
	@Test
	public void returnsEmptyExclusionsWithoutConfig() throws Exception {
		CustomFieldsAdapter adapter = new CustomFieldsAdapter();

		assertThat(adapter.getExcludedFieldKeyNames(), is(empty()));
	}

	@Test
	public void providesGivenCustomFields() throws Exception {
		CustomFieldsAdapter adapter = new CustomFieldsAdapter(CustomField.newBuilder().setKey("this key").build(),
				CustomField.newBuilder().setKey("that key").build());

		assertThat(adapter.getCustomFieldKeyNames(), containsInAnyOrder("this key", "that key"));
	}

	@Test
	public void excludesGivenCustomFields() throws Exception {
		CustomFieldsAdapter adapter = new CustomFieldsAdapter(CustomField.newBuilder().setKey("this key").build(),
				CustomField.newBuilder().setKey("that key").build());

		assertThat(adapter.getExcludedFieldKeyNames(), containsInAnyOrder("this key", "that key"));
	}

	@Test
	public void removesCustomFieldsFromExclusionsWhenRetained() throws Exception {
		CustomFieldsAdapter adapter = new CustomFieldsAdapter(
				CustomField.newBuilder().setKey("this key").setRetainOriginal(true).build(),
				CustomField.newBuilder().setKey("that key").build());

		assertThat(adapter.getExcludedFieldKeyNames(), contains("that key"));
	}

	@Test
	public void neverExcludesLogContextFieldsEvenWhenConfigured() throws Exception {
		List<CustomField> customFields = new ArrayList<>(LogContext.getContextFieldsKeys().size());
		for (String key : LogContext.getContextFieldsKeys()) {
			customFields.add(CustomField.newBuilder().setKey(key).build());
		}
		CustomFieldsAdapter adapter = new CustomFieldsAdapter(
				customFields.toArray(new CustomField[customFields.size()]));

		assertThat(adapter.getExcludedFieldKeyNames(), is(empty()));
	}
}

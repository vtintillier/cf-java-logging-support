
package com.sap.hcp.cf.logback.converter;

import static com.sap.hcp.cf.logback.converter.CustomFieldsAdapter.OPTION_MDC_CUSTOM_FIELDS;
import static com.sap.hcp.cf.logback.converter.CustomFieldsAdapter.OPTION_MDC_RETAINED_FIELDS;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hcp.cf.logging.common.LogContext;

import ch.qos.logback.core.Context;

@RunWith(MockitoJUnitRunner.class)
public class CustomFieldsAdapterTest {

	@SuppressWarnings("serial")
	private static final Map<String, String> ALL_ENTRIES = new HashMap<String, String>() {
		{
			put("this key", "this value");
			put("that key", "that value");
			put("other key", "other value");
		}
	};

	@Mock
	private Context context;

	@InjectMocks
	private CustomFieldsAdapter adapter;

	@Test
	public void rejectsAllFieldsWithoutContext() throws Exception {
		adapter.initialize(null);

		Map<String, String> selected = adapter.selectCustomFields(ALL_ENTRIES);

		assertThat(selected.entrySet(), is(empty()));
	}

	@Test
	public void rejectsAllFieldsWithoutConfigObject() throws Exception {
		adapter.initialize(context);

		Map<String, String> selected = adapter.selectCustomFields(ALL_ENTRIES);

		assertThat(selected.entrySet(), is(empty()));
	}

	@Test
	public void rejectsAllFieldsWithImproperConfigObject() throws Exception {
		when(context.getObject(OPTION_MDC_CUSTOM_FIELDS)).thenReturn(new Object());
		adapter.initialize(context);

		Map<String, String> selected = adapter.selectCustomFields(ALL_ENTRIES);

		assertThat(selected.entrySet(), is(empty()));
	}

	@Test
	public void rejectsAllFieldsWithEmptyList() throws Exception {
		when(context.getObject(OPTION_MDC_CUSTOM_FIELDS)).thenReturn(Collections.emptyList());
		adapter.initialize(context);

		Map<String, String> selected = adapter.selectCustomFields(ALL_ENTRIES);

		assertThat(selected.entrySet(), is(empty()));
	}

	@Test
	public void selectsConfiguredFields() throws Exception {
		when(context.getObject(OPTION_MDC_CUSTOM_FIELDS)).thenReturn(asList("this key", "that key"));
		adapter.initialize(context);

		Map<String, String> selected = adapter.selectCustomFields(ALL_ENTRIES);

		assertThat(selected, allOf(hasEntry("this key", "this value"), hasEntry("that key", "that value"),
				not(hasEntry("other key", "other value"))));
	}

	@Test
	public void selectsEmptyMapOnNullInput() throws Exception {
		Map<String, String> selected = adapter.selectCustomFields(null);

		assertThat(selected.entrySet(), is(empty()));
	}

	@Test
	public void emptyExclusionsWithoutContext() throws Exception {
		adapter.initialize(null);

		List<String> exclusions = adapter.getCustomFieldExclusions();

		assertThat(exclusions, is(empty()));
	}

	@Test
	public void emptyExclusionsWithoutConfigObject() throws Exception {
		adapter.initialize(context);

		List<String> exclusions = adapter.getCustomFieldExclusions();

		assertThat(exclusions, is(empty()));
	}

	@Test
	public void emptyExclusionsWithImproperCustomFields() throws Exception {
		when(context.getObject(OPTION_MDC_CUSTOM_FIELDS)).thenReturn(new Object());
		adapter.initialize(context);

		List<String> exclusions = adapter.getCustomFieldExclusions();

		assertThat(exclusions, is(empty()));
	}

	@Test
	public void emptyExclusionsWithImproperRetains() throws Exception {
		when(context.getObject(OPTION_MDC_RETAINED_FIELDS)).thenReturn(new Object());
		adapter.initialize(context);

		List<String> exclusions = adapter.getCustomFieldExclusions();

		assertThat(exclusions, is(empty()));
	}

	@Test
	public void emptyExclusionsWithoutRetains() throws Exception {
		adapter.initialize(context);

		List<String> exclusions = adapter.getCustomFieldExclusions();

		assertThat(exclusions, is(empty()));
	}

	@Test
	public void excludesCustomFields() throws Exception {
		when(context.getObject(OPTION_MDC_CUSTOM_FIELDS)).thenReturn(asList("this key", "that key"));
		adapter.initialize(context);

		List<String> exclusions = adapter.getCustomFieldExclusions();

		assertThat(exclusions, containsInAnyOrder("this key", "that key"));

	}

	@Test
	public void removesCustomFieldsFromExclusionsWhenRetained() throws Exception {
		when(context.getObject(OPTION_MDC_CUSTOM_FIELDS)).thenReturn(asList("this key", "that key"));
		when(context.getObject(OPTION_MDC_RETAINED_FIELDS)).thenReturn(asList("that key"));
		adapter.initialize(context);

		List<String> exclusions = adapter.getCustomFieldExclusions();

		assertThat(exclusions, containsInAnyOrder("this key"));
	}

	@Test
	public void neverExcludesLogContextFieldsEvenWhenConfigured() throws Exception {
		when(context.getObject(OPTION_MDC_RETAINED_FIELDS)).thenReturn(LogContext.getContextFieldsKeys());
		adapter.initialize(context);

		List<String> exclusions = adapter.getCustomFieldExclusions();

		assertThat(exclusions, is(empty()));
	}

}

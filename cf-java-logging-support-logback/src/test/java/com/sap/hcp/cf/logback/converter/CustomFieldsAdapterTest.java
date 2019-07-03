
package com.sap.hcp.cf.logback.converter;

import static com.sap.hcp.cf.logback.converter.CustomFieldsAdapter.OPTION_MDC_CUSTOM_FIELDS;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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

		assertThat(selected,
				allOf(hasEntry("this key", "this value"), hasEntry("that key", "that value"),
						not(hasEntry("other key", "other value"))));
	}

	@Test
	public void selectsEmptyMapOnNullInput() throws Exception {
		Map<String, String> selected = adapter.selectCustomFields(null);

		assertThat(selected.entrySet(), is(empty()));
	}
}

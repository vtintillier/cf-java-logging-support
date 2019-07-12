package com.sap.hcp.cf.log4j2.converter;

import static com.sap.hcp.cf.logging.common.customfields.CustomField.customField;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hcp.cf.logging.common.converter.DefaultCustomFieldsConverter;
import com.sap.hcp.cf.logging.common.customfields.CustomField;

@RunWith(MockitoJUnitRunner.class)
public class CustomFieldsConverterTest {

	private static final Object[] NO_PARAMETERS = new Object[0];
	private static final String[] CUSTOM_FIELD_KEYS = new String[] { "this key", "that key" };
	@SuppressWarnings("serial")
	private static Map<String, String> MDC_PROPERTIES = new HashMap<String, String>() {
		{
			put("this key", "this value");
			put("that key", "that value");
			put("other key", "other value");
		}
	};

	@Mock
	private DefaultCustomFieldsConverter defaultConverter;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private LogEvent event;

	@Captor
	private ArgumentCaptor<Map<String, String>> mdcFields;

	@Captor
	private ArgumentCaptor<Object> arguments;

	private CustomFieldsConverter converter;

	@Before
	public void initializeConverter() {
		this.converter = new CustomFieldsConverter(CUSTOM_FIELD_KEYS);
		converter.setConverter(defaultConverter);
	}

	@Test
	public void forwardsStringBuilder() throws Exception {
		StringBuilder sb = new StringBuilder();

		converter.format(event, sb);

		verify(defaultConverter).convert(same(sb), any(), any());
	}

	@Test
	public void emptyMdcAndArguments() throws Exception {
		StringBuilder sb = new StringBuilder();
		when(event.getContextData()).thenReturn(new SortedArrayStringMap());
		when(event.getMessage().getParameters()).thenReturn(NO_PARAMETERS);

		converter.format(event, sb);

		verifyConverterCall(emptyMap());
	}

	@Test
	public void standardArgument() throws Exception {
		StringBuilder sb = new StringBuilder();
		when(event.getContextData()).thenReturn(new SortedArrayStringMap());
		when(event.getMessage().getParameters()).thenReturn(new Object[] { "standard argument" });

		converter.format(event, sb);

		verifyConverterCall(emptyMap(), is("standard argument"));
	}

	@Test
	public void singleCustomFieldArgument() throws Exception {
		StringBuilder sb = new StringBuilder();
		when(event.getContextData()).thenReturn(new SortedArrayStringMap());
		CustomField customField = customField("some key", "some value");
		when(event.getMessage().getParameters()).thenReturn(new Object[] { customField });

		converter.format(event, sb);

		verifyConverterCall(emptyMap(), is(sameInstance(customField)));
	}

	@Test
	public void mdcFields() throws Exception {
		StringBuilder sb = new StringBuilder();
		when(event.getContextData()).thenReturn(new SortedArrayStringMap(MDC_PROPERTIES));
		when(event.getMessage().getParameters()).thenReturn(NO_PARAMETERS);

		converter.format(event, sb);

		verifyConverterCall(allOf(hasEntry("this key", "this value"), hasEntry("that key", "that value"),
				not(hasEntry("other key", "other value"))));
	}

	@Test
	public void mergesMdcFieldsAndArguments() throws Exception {
		StringBuilder sb = new StringBuilder();
		when(event.getContextData()).thenReturn(new SortedArrayStringMap(MDC_PROPERTIES));
		CustomField customField = customField("some key", "some value");
		when(event.getMessage().getParameters()).thenReturn(new Object[] { customField });

		converter.format(event, sb);

		verifyConverterCall(allOf(hasEntry("this key", "this value"), hasEntry("that key", "that value"),
				not(hasEntry("other key", "other value"))), is(sameInstance(customField)));

	}

	private static Matcher<Map<? extends String, ? extends String>> emptyMap() {
		return not(hasEntry(anyString(), anyString()));
	}

	private static Matcher<String> anyString() {
		return org.hamcrest.Matchers.any(String.class);
	}

	private void verifyConverterCall(Matcher<Map<? extends String, ? extends String>> mdcFieldsMatcher) {
		verify(defaultConverter).convert(any(), mdcFields.capture());
		assertThat(mdcFields.getValue(), mdcFieldsMatcher);
	}

	private void verifyConverterCall(Matcher<Map<? extends String, ? extends String>> mdcFieldsMatcher,
			Matcher<Object> argumentsMatcher) {
		verify(defaultConverter).convert(any(), mdcFields.capture(), arguments.capture());
		assertThat(mdcFields.getValue(), mdcFieldsMatcher);
		assertThat(arguments.getValue(), argumentsMatcher);
	}

}

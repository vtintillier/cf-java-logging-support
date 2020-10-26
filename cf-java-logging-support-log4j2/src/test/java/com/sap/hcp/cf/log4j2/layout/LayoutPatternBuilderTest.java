package com.sap.hcp.cf.log4j2.layout;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;

import java.util.Arrays;
import java.util.Collections;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.junit.Test;

public class LayoutPatternBuilderTest {

	private static final String COMMON_PREFIX = "{ \"written_at\":\"%d{yyyy-MM-dd'T'HH:mm:ss.SSSX}{UTC}\",\"written_ts\":%tstamp";
	private static final String COMMON_SUFFIX = "}%n";

	@Test
	public void minimalPattern() throws Exception {
		String pattern = new LayoutPatternBuilder().build();

		assertThat(pattern, is(COMMON_PREFIX + COMMON_SUFFIX));
		assertThat(pattern, specificPart(is(isEmptyString())));
	}

	@Test
	public void requestPattern() throws Exception {
		String pattern = new LayoutPatternBuilder().addRequestMetrics().build();

		assertThat(pattern, specificPart(is(",\"type\":\"request\",%jsonmsg{flatten}")));
	}

	@Test
	public void basicApplicationLogs() throws Exception {
		String pattern = new LayoutPatternBuilder().addBasicApplicationLogs().build();

		assertThat(pattern, specificPart(is(
				",\"type\":\"log\",\"logger\":\"%replace{%logger}{\"}{\\\\\"}\",\"thread\":\"%replace{%thread}{\"}{\\\\\"}\",\"level\":\"%p\",\"categories\":%categories,\"msg\":%jsonmsg{escape}")));
	}

	@Test
	public void contextProperties() throws Exception {
		String pattern = new LayoutPatternBuilder().addContextProperties(Arrays.asList("this key", "that key")).build();

		assertThat(pattern, specificPart(is(",%ctxp{this key}{that key}")));
	}

	@Test
	public void customFields() throws Exception {
		String pattern = new LayoutPatternBuilder().addCustomFields(Arrays.asList("this key", "that key")).build();

		assertThat(pattern, specificPart(is(",\"#cf\":{%cf{this key}{that key}}")));
	}

	@Test
	public void emptyCustomFields() throws Exception {
		String pattern = new LayoutPatternBuilder().addCustomFields(Collections.emptyList()).build();

		assertThat(pattern, specificPart(is("")));
	}

	@Test
	public void nullCustomFields() throws Exception {
		String pattern = new LayoutPatternBuilder().addCustomFields(null).build();

		assertThat(pattern, specificPart(is("")));
	}

	@Test
	public void stacktrace() throws Exception {
		String pattern = new LayoutPatternBuilder().addStacktraces().build();

		assertThat(pattern, specificPart(is(",\"stacktrace\":%stacktrace")));
	}

	@Test
	public void suppressExceptions() throws Exception {
		String pattern = new LayoutPatternBuilder().suppressExceptions().build();

		assertThat(pattern, specificPart(is("%ex{0} ")));
	}

	@Test
	public void requestMetricsScenario() throws Exception {
		String pattern = new LayoutPatternBuilder().addRequestMetrics().addContextProperties(emptyList())
				.suppressExceptions().build();

		assertThat(pattern, specificPart(is(",\"type\":\"request\",%jsonmsg{flatten},%ctxp{}%ex{0} ")));
	}

	@Test
	public void applicationScenario() throws Exception {
		String pattern = new LayoutPatternBuilder().addBasicApplicationLogs()
				.addContextProperties(asList("excluded-field")).addCustomFields(asList("custom-field"))
				.suppressExceptions().build();

		assertThat(pattern, specificPart(is(
				",\"type\":\"log\",\"logger\":\"%replace{%logger}{\"}{\\\\\"}\",\"thread\":\"%replace{%thread}{\"}{\\\\\"}\",\"level\":\"%p\",\"categories\":%categories,\"msg\":%jsonmsg{escape},%ctxp{excluded-field},\"#cf\":{%cf{custom-field}}%ex{0} ")));
	}

	@Test
	public void exceptionScenario() throws Exception {
		String pattern = new LayoutPatternBuilder().addBasicApplicationLogs()
				.addContextProperties(asList("excluded-field")).addCustomFields(asList("custom-field")).addStacktraces()
				.build();

		assertThat(pattern, specificPart(is(
				",\"type\":\"log\",\"logger\":\"%replace{%logger}{\"}{\\\\\"}\",\"thread\":\"%replace{%thread}{\"}{\\\\\"}\",\"level\":\"%p\",\"categories\":%categories,\"msg\":%jsonmsg{escape},%ctxp{excluded-field},\"#cf\":{%cf{custom-field}},\"stacktrace\":%stacktrace")));
	}

	private static Matcher<String> specificPart(Matcher<String> expected) {
		return new FeatureMatcher<String, String>(expected, "specific part", "specific part") {

			@Override
			protected String featureValueOf(String fullPattern) {
				return fullPattern.substring(COMMON_PREFIX.length(), fullPattern.length() - COMMON_SUFFIX.length());
			}
		};
	}
}

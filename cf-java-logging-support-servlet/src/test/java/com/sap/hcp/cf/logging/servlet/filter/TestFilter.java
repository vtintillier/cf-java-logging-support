package com.sap.hcp.cf.logging.servlet.filter;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.jr.ob.JSON;
import com.sap.hcp.cf.logging.common.Defaults;
import com.sap.hcp.cf.logging.common.Fields;
import com.sap.hcp.cf.logging.common.HttpHeaders;
import com.sap.hcp.cf.logging.servlet.filter.RequestLoggingFilter;

public class TestFilter {

	protected final ByteArrayOutputStream outContent  = new ByteArrayOutputStream();
    protected final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    private static final String REQUEST_ID = "1234-56-7890-xxx";
    private static final String CORRELATION_ID = "xxx-56-7890-xxx";
    private static final String REQUEST = "/foobar";
    private static final String QUERY_STRING = "baz=bla";
    private static final String FULL_REQUEST = REQUEST + "?" + QUERY_STRING;
    private static final String REMOTE_HOST = "acme.org";
    private static final String REFERER = "my.fancy.com";
        
	@Before
	public void setupStreams() {
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}
	
	@After
	public void teardownStreams() {
        System.setOut(null);
        System.setErr(null);		
	}

	@Test
	public void testSimple() {
		HttpServletRequest mockReq = mock(HttpServletRequest.class);
		HttpServletResponse mockResp = mock(HttpServletResponse.class);
		PrintWriter mockWriter = mock(PrintWriter.class);
		try {
			when(mockResp.getWriter()).thenReturn(mockWriter);
		} catch (IOException e1) {
			System.err.println("mocking filter failed" + e1.getMessage());
		}
		FilterChain mockFilterChain = mock(FilterChain.class);
		try {
			new RequestLoggingFilter().doFilter(mockReq, mockResp, mockFilterChain);
			assertThat(getField(Fields.REQUEST), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.CORRELATION_ID), not(isEmptyOrNullString()));
			assertThat(getField(Fields.REQUEST_ID), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.REMOTE_HOST), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.COMPONENT_ID), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.CONTAINER_ID), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.REFERER), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.REQUEST_SIZE_B), is("-1"));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testInputStream() {
		HttpServletRequest mockReq = mock(HttpServletRequest.class);
		HttpServletResponse mockResp = mock(HttpServletResponse.class);
		PrintWriter mockWriter = mock(PrintWriter.class);
		ServletInputStream mockStream = mock(ServletInputStream.class);

		try {
			when(mockResp.getWriter()).thenReturn(mockWriter);
			when(mockReq.getInputStream()).thenReturn(mockStream);
			when(mockStream.read()).thenReturn(1);
		} catch (IOException e1) {
			System.err.println("mocking filter failed" + e1.getMessage());
		}
		FilterChain mockFilterChain = new FilterChain() {
			@Override
			public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
				request.getInputStream().read();
			}
		};
		try {
			new RequestLoggingFilter().doFilter(mockReq, mockResp, mockFilterChain);
			assertThat(getField(Fields.REQUEST), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.CORRELATION_ID), not(isEmptyOrNullString()));
			assertThat(getField(Fields.REQUEST_ID), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.REMOTE_HOST), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.COMPONENT_ID), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.CONTAINER_ID), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.REFERER), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.REQUEST_SIZE_B), is("1"));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testReader() {
		HttpServletRequest mockReq = mock(HttpServletRequest.class);
		HttpServletResponse mockResp = mock(HttpServletResponse.class);
		PrintWriter mockWriter = mock(PrintWriter.class);
		BufferedReader mockReader = mock(BufferedReader.class);

		try {
			when(mockResp.getWriter()).thenReturn(mockWriter);
			when(mockReq.getReader()).thenReturn(mockReader);
			when(mockReader.read()).thenReturn(1);
		} catch (IOException e1) {
			System.err.println("mocking filter failed" + e1.getMessage());
		}
		FilterChain mockFilterChain = new FilterChain() {
			@Override
			public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
				request.getReader().read();
			}
		};
		try {
			new RequestLoggingFilter().doFilter(mockReq, mockResp, mockFilterChain);
			assertThat(getField(Fields.REQUEST), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.CORRELATION_ID), not(isEmptyOrNullString()));
			assertThat(getField(Fields.REQUEST_ID), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.REMOTE_HOST), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.COMPONENT_ID), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.CONTAINER_ID), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.REFERER), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.REQUEST_SIZE_B), is("1"));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testWithSettings() {
		HttpServletRequest mockReq = mock(HttpServletRequest.class);
		HttpServletResponse mockResp = mock(HttpServletResponse.class);
		PrintWriter mockWriter = mock(PrintWriter.class);
		try {
			when(mockResp.getWriter()).thenReturn(mockWriter);
			when(mockReq.getRequestURI()).thenReturn(REQUEST);
			when(mockReq.getQueryString()).thenReturn(QUERY_STRING);
			when(mockReq.getRemoteHost()).thenReturn(REMOTE_HOST);
			when(mockReq.getHeader(HttpHeaders.X_VCAP_REQUEST_ID)).thenReturn(REQUEST_ID); // will also set correlation id
			when(mockReq.getHeader(HttpHeaders.REFERER)).thenReturn(REFERER);
		} catch (IOException e1) {
			System.err.println("mocking filter failed" + e1.getMessage());
		}
		FilterChain mockFilterChain = mock(FilterChain.class);
		try {
			new RequestLoggingFilter().doFilter(mockReq, mockResp, mockFilterChain);
			assertThat(getField(Fields.REQUEST), is(FULL_REQUEST));
			assertThat(getField(Fields.CORRELATION_ID), is(REQUEST_ID));
			assertThat(getField(Fields.REQUEST_ID), is(REQUEST_ID));
			assertThat(getField(Fields.REMOTE_HOST), is(REMOTE_HOST));
			assertThat(getField(Fields.COMPONENT_ID), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.CONTAINER_ID), is(Defaults.UNKNOWN));
			assertThat(getField(Fields.REFERER), is(REFERER));
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testExplicitCorrelationId() {
		HttpServletRequest mockReq = mock(HttpServletRequest.class);
		HttpServletResponse mockResp = mock(HttpServletResponse.class);
		PrintWriter mockWriter = mock(PrintWriter.class);
		try {
			when(mockResp.getWriter()).thenReturn(mockWriter);
			when(mockReq.getHeader(HttpHeaders.CORRELATION_ID)).thenReturn(CORRELATION_ID); 
			when(mockReq.getHeader(HttpHeaders.X_VCAP_REQUEST_ID)).thenReturn(REQUEST_ID); 
		} catch (IOException e1) {
			System.err.println("mocking filter failed" + e1.getMessage());
		}
		FilterChain mockFilterChain = mock(FilterChain.class);
		try {
			new RequestLoggingFilter().doFilter(mockReq, mockResp, mockFilterChain);
			assertThat(getField(Fields.CORRELATION_ID), is(CORRELATION_ID));
			assertThat(getField(Fields.CORRELATION_ID), not(REQUEST_ID));
			
		}
		catch (Exception e) {
			fail(e.getMessage());
		}
	}
		
	protected String getField(String fieldName) {
		try {
			/* -- we may have more than one line, just take the last -- */
			return JSON.std.mapFrom(getLastLine()).get(fieldName).toString();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private String getLastLine() {
		String[] lines = this.outContent.toString().split("\n");
		return lines[lines.length-1];
	}
	
}

package com.sap.hcp.cf.logging.servlet.filter;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;
import com.sap.hcp.cf.logging.common.Defaults;
import com.sap.hcp.cf.logging.common.Fields;
import com.sap.hcp.cf.logging.common.HttpHeaders;

public class RequestLoggingFilterTest {

    protected final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    protected final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private PrintStream previousOut;
    private PrintStream previousErr;

    private static final String REQUEST_ID = "1234-56-7890-xxx";
    private static final String CORRELATION_ID = "xxx-56-7890-xxx";
    private static final String REQUEST = "/foobar";
    private static final String QUERY_STRING = "baz=bla";
    private static final String FULL_REQUEST = REQUEST + "?" + QUERY_STRING;
    private static final String REMOTE_HOST = "acme.org";
    private static final String REFERER = "my.fancy.com";

    @Before
    public void setupStreams() {
        previousOut = System.out;
        System.setOut(new PrintStream(outContent));
        previousErr = System.err;
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void teardownStreams() {
        System.setOut(previousOut);
        System.setErr(previousErr);
    }

    @Test
    public void testSimple() throws IOException, ServletException {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockResp = mock(HttpServletResponse.class);
        PrintWriter mockWriter = mock(PrintWriter.class);
        when(mockResp.getWriter()).thenReturn(mockWriter);
        FilterChain mockFilterChain = mock(FilterChain.class);
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

    @Test
    public void testInputStream() throws IOException, ServletException {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockResp = mock(HttpServletResponse.class);
        PrintWriter mockWriter = mock(PrintWriter.class);
        ServletInputStream mockStream = mock(ServletInputStream.class);

        when(mockResp.getWriter()).thenReturn(mockWriter);
        when(mockReq.getInputStream()).thenReturn(mockStream);
        when(mockStream.read()).thenReturn(1);
        FilterChain mockFilterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException,
                                                                                   ServletException {
                request.getInputStream().read();
            }
        };
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

    @Test
    public void testReader() throws IOException, ServletException {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockResp = mock(HttpServletResponse.class);
        PrintWriter mockWriter = mock(PrintWriter.class);
        BufferedReader mockReader = mock(BufferedReader.class);

        when(mockResp.getWriter()).thenReturn(mockWriter);
        when(mockReq.getReader()).thenReturn(mockReader);
        when(mockReader.read()).thenReturn(1);
        FilterChain mockFilterChain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException,
                                                                                   ServletException {
                request.getReader().read();
            }
        };
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

    @Test
    public void testWithSettings() throws IOException, ServletException {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockResp = mock(HttpServletResponse.class);
        PrintWriter mockWriter = mock(PrintWriter.class);
        when(mockResp.getWriter()).thenReturn(mockWriter);
        when(mockReq.getRequestURI()).thenReturn(REQUEST);
        when(mockReq.getQueryString()).thenReturn(QUERY_STRING);
        when(mockReq.getRemoteHost()).thenReturn(REMOTE_HOST);
        // will also set correlation id
        when(mockReq.getHeader(HttpHeaders.X_VCAP_REQUEST_ID)).thenReturn(REQUEST_ID);
        when(mockReq.getHeader(HttpHeaders.REFERER)).thenReturn(REFERER);
        FilterChain mockFilterChain = mock(FilterChain.class);
        new RequestLoggingFilter().doFilter(mockReq, mockResp, mockFilterChain);
        assertThat(getField(Fields.REQUEST), is(FULL_REQUEST));
        assertThat(getField(Fields.CORRELATION_ID), is(REQUEST_ID));
        assertThat(getField(Fields.REQUEST_ID), is(REQUEST_ID));
        assertThat(getField(Fields.REMOTE_HOST), is(REMOTE_HOST));
        assertThat(getField(Fields.COMPONENT_ID), is(Defaults.UNKNOWN));
        assertThat(getField(Fields.CONTAINER_ID), is(Defaults.UNKNOWN));
        assertThat(getField(Fields.REFERER), is(REFERER));
    }

    @Test
    public void testExplicitCorrelationId() throws IOException, ServletException {
        HttpServletRequest mockReq = mock(HttpServletRequest.class);
        HttpServletResponse mockResp = mock(HttpServletResponse.class);
        PrintWriter mockWriter = mock(PrintWriter.class);
        when(mockResp.getWriter()).thenReturn(mockWriter);
        when(mockReq.getHeader(HttpHeaders.CORRELATION_ID)).thenReturn(CORRELATION_ID);
        when(mockReq.getHeader(HttpHeaders.X_VCAP_REQUEST_ID)).thenReturn(REQUEST_ID);
        FilterChain mockFilterChain = mock(FilterChain.class);
        new RequestLoggingFilter().doFilter(mockReq, mockResp, mockFilterChain);
        assertThat(getField(Fields.CORRELATION_ID), is(CORRELATION_ID));
        assertThat(getField(Fields.CORRELATION_ID), not(REQUEST_ID));
    }

    protected String getField(String fieldName) throws JSONObjectException, IOException {
        return JSON.std.mapFrom(getLastLine()).get(fieldName).toString();
    }

    private String getLastLine() {
        String[] lines = outContent.toString().split("\n");
        return lines[lines.length - 1];
    }
}

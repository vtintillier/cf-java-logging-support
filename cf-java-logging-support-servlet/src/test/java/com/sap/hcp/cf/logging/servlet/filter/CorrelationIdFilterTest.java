package com.sap.hcp.cf.logging.servlet.filter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.UUID;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.MDC;

import com.sap.hcp.cf.logging.common.request.HttpHeader;
import com.sap.hcp.cf.logging.common.request.HttpHeaders;

@RunWith(MockitoJUnitRunner.class)
public class CorrelationIdFilterTest {

    private static String KNOWN_CORRELATION_ID = UUID.randomUUID().toString();

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;

    private ContextMapExtractor mdcExtractor;

    @Before
    public void setUp() throws Exception {
        MDC.clear();
        mdcExtractor = new ContextMapExtractor();
        doAnswer(mdcExtractor).when(chain).doFilter(request, response);
    }

    public String getExtractedCorrelationId() {
        return mdcExtractor.getField(HttpHeaders.CORRELATION_ID.getField());
    }

    @Test
    public void addsKnownCorrelationIdToMDC() throws Exception {
        when(request.getHeader(HttpHeaders.CORRELATION_ID.getName())).thenReturn(KNOWN_CORRELATION_ID);

        new CorrelationIdFilter().doFilter(request, response, chain);

        assertThat(getExtractedCorrelationId(), is(equalTo(KNOWN_CORRELATION_ID)));
    }

    @Test
    public void addsGeneratedCorrelationIdToMDC() throws Exception {

        new CorrelationIdFilter().doFilter(request, response, chain);

        assertThat(getExtractedCorrelationId(), is(not(nullValue())));
        assertThat(getExtractedCorrelationId(), is(not(equalTo(KNOWN_CORRELATION_ID))));
    }

    @Test
    public void removesCorrelationIdAfterFiltering() throws Exception {
        new CorrelationIdFilter().doFilter(request, response, chain);

        assertThat(MDC.get(HttpHeaders.CORRELATION_ID.getField()), is(nullValue()));
    }

    @Test
    public void addsKnownCorrelationIdAsResponseHeader() throws Exception {
        when(request.getHeader(HttpHeaders.CORRELATION_ID.getName())).thenReturn(KNOWN_CORRELATION_ID);

        new CorrelationIdFilter().doFilter(request, response, chain);

        verify(response).setHeader(HttpHeaders.CORRELATION_ID.getName(), KNOWN_CORRELATION_ID);
    }

    @Test
    public void doesNotAddCorrelationIdToCommittedResponse() throws Exception {
        when(response.isCommitted()).thenReturn(true);

        new CorrelationIdFilter().doFilter(request, response, chain);

        verify(response).isCommitted();
        verifyNoMoreInteractions(response);
    }

    @Test
    public void doesNotOverwriteCorrelationIdInResponse() throws Exception {
        when(response.isCommitted()).thenReturn(false);
        when(response.getHeader(HttpHeaders.CORRELATION_ID.getName())).thenReturn("preexisting-correlation-id");

        new CorrelationIdFilter().doFilter(request, response, chain);

        verify(response).isCommitted();
        verify(response).getHeader(HttpHeaders.CORRELATION_ID.getName());
        verifyNoMoreInteractions(response);
    }

    @Test
    public void usesCustomHeader() throws Exception {
        HttpHeader myHeader = new HttpTestHeader("my-header", "my-field", null, false);
        when(request.getHeader("my-header")).thenReturn(KNOWN_CORRELATION_ID);

        new CorrelationIdFilter(myHeader).doFilter(request, response, chain);

        assertThat(mdcExtractor.getField("my-field"), is(equalTo(KNOWN_CORRELATION_ID)));
        verify(response).setHeader("my-header", KNOWN_CORRELATION_ID);
    }
}

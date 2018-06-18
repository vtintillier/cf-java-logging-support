package com.sap.hcp.cf.logging.servlet.filter;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.hcp.cf.logging.common.Fields;
import com.sap.hcp.cf.logging.common.HttpHeaders;
import com.sap.hcp.cf.logging.common.RequestRecord;
import com.sap.hcp.cf.logging.common.Value;

@RunWith(MockitoJUnitRunner.class)
public class RequestLoggingVisitorTest {

	@Mock
	private ContentLengthTrackingResponseWrapper responseWrapper;

	@Mock
	private RequestRecord requestRecord;

	@Mock
	private HttpServletRequest httpRequest;

	@Mock
	private HttpServletResponse httpResponse;

	@InjectMocks
	private RequestLoggingVisitor visitor;

	@Captor
	private ArgumentCaptor<Value> valueCaptor;

	@Test
	public void stopsRequestRecord() throws Exception {
		visitor.logRequest(httpRequest, httpResponse);
		verify(requestRecord).stop();
	}

	@Test
	public void addsHttpStatusAsValue() throws Exception {
		when(httpResponse.getStatus()).thenReturn(123);
		visitor.logRequest(httpRequest, httpResponse);
		verify(requestRecord).addValue(eq(Fields.RESPONSE_STATUS), valueCaptor.capture());
		assertThat(valueCaptor.getValue().asLong(), is(123L));
	}

	@Test
	public void addsResponseContentTypeAsTag() throws Exception {
		when(httpResponse.getHeader(HttpHeaders.CONTENT_TYPE)).thenReturn("application/vnd.test");
		visitor.logRequest(httpRequest, httpResponse);
		verify(requestRecord).addTag(Fields.RESPONSE_CONTENT_TYPE, "application/vnd.test");
	}

	@Test
	public void addsRequestContentLengthAsValue() throws Exception {
		when(httpRequest.getContentLength()).thenReturn(12345);
		visitor.logRequest(httpRequest, httpResponse);
		verify(requestRecord).addValue(eq(Fields.REQUEST_SIZE_B), valueCaptor.capture());
		assertThat(valueCaptor.getValue().asLong(), is(12345L));
	}

	@Test
	public void addsResponseContentLengthAsValueFromHeaderIfAvailable() throws Exception {
		when(httpResponse.getHeader(HttpHeaders.CONTENT_LENGTH)).thenReturn("1234");
		visitor.logRequest(httpRequest, httpResponse);
		verify(requestRecord).addValue(eq(Fields.RESPONSE_SIZE_B), valueCaptor.capture());
		verifyZeroInteractions(responseWrapper);
		assertThat(valueCaptor.getValue().asLong(), is(1234L));
	}

	@Test
	public void addsResponseContentLengthAsValueFromWrapperAsFAllback() throws Exception {
		when(responseWrapper.getContentLength()).thenReturn(1234L);
		visitor.logRequest(httpRequest, httpResponse);
		verify(requestRecord).addValue(eq(Fields.RESPONSE_SIZE_B), valueCaptor.capture());
		assertThat(valueCaptor.getValue().asLong(), is(1234L));
	}

}

package com.sap.hcp.cf.logging.jersey.filter;

import static com.sap.hcp.cf.logging.common.LogContext.HTTP_HEADER_CORRELATION_ID;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.sap.hcp.cf.logging.common.Defaults;
import com.sap.hcp.cf.logging.common.Fields;
import com.sap.hcp.cf.logging.common.HttpHeaders;
import com.sap.hcp.cf.logging.common.RequestRecord.Direction;


/**
 * Test Class for Jersey Performance Logs
 * 
 * @author d048888
 *
 */
public class RequestMetricsClientFilterTest extends AbstractFilterTest   {


	
	@Override
	protected void configureClient(ClientConfig clientConfig) {
		/* -- make sure client side logging is registered -- */
		RequestMetricsFilterRegistry.registerClientFilters(clientConfig);
	}


	@Override
	protected Application configure() {
		ResourceConfig config = new ResourceConfig();
		config.register(TestResource.class);
		config.register(TestChainedResource.class);
		return config;

	}

	@Test
	public void ChainedResourcePerformanceLogTest() {
		final String CORRELATION_ID = "test-1234-5678";
		final Response response = target("testchainedresource").request().header(HttpHeaders.CORRELATION_ID, CORRELATION_ID).get();
		if (response.hasEntity()) {
			String res = response.readEntity(String.class);
			res.length();
		}
		assertThat(getLogSize(), is(2));
		assertThat(response.getStatus(), is(200));	
		/*
		 * -- correlation id should have been propagated
		 */
		for (int i = 0; i < getLogSize(); i++) {
			assertThat(getField(Fields.CORRELATION_ID, i), is(CORRELATION_ID));
		}
	}

	@Test
	public void PerformanceLogTest() {		
		@SuppressWarnings("unused")
		final Response response = getClient().target(getBaseUri() + "testresource").request().header(HTTP_HEADER_CORRELATION_ID, "1").get();

		assertThat(getField(Fields.COMPONENT_ID), is(Defaults.UNKNOWN));
		assertThat(getField(Fields.RESPONSE_SIZE_B), is("4"));
		assertThat(getField(Fields.RESPONSE_TIME_MS), not(nullValue()));
		assertThat(getField(Fields.RESPONSE_STATUS), is(Integer.toString(TestResource.EXPECTED_STATUS_CODE)));
		assertThat(getField(Fields.RESPONSE_CONTENT_TYPE), is(TestResource.EXPECTED_CONTENT_TYPE));
		assertThat(getField(Fields.DIRECTION), is(Direction.OUT.toString()));
		assertThat(getField(Fields.METHOD), is(TestResource.EXPECTED_REQUEST_METHOD));
		assertThat(getField(Fields.REMOTE_IP), not(nullValue()));
		assertThat(getField(Fields.REMOTE_HOST), not(nullValue()));
		assertThat(getField(Fields.REFERER), not(nullValue()));
		assertThat(getField(Fields.X_FORWARDED_FOR), not(nullValue()));
		assertThat(getField(Fields.LAYER), is(ClientRequestContextAdapter.LAYER_NAME));
	}

	@Test
	public void ResponseTimeTest() {		
		@SuppressWarnings("unused")
		final Response response = target("testresource").request().delete();

		assertThat(new Double(getField(Fields.RESPONSE_TIME_MS)), greaterThan(TestResource.EXPECTED_REQUEST_TIME));
		
	}
}




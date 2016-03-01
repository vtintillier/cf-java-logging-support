package com.sap.hcp.cf.logging.jersey.filter;

import static com.sap.hcp.cf.logging.common.LogContext.HTTP_HEADER_CORRELATION_ID;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;

import com.sap.hcp.cf.logging.common.Fields;
import com.sap.hcp.cf.logging.common.RequestRecord.Direction;
import com.sap.hcp.cf.logging.jersey.filter.ContainerRequestContextAdapter;
import com.sap.hcp.cf.logging.jersey.filter.PerfXFilterRegistry;


/**
 * Test Class for Jersey Performance Logs
 * 
 * @author d048888
 *
 */
public class PerfXContainerFilterTest extends AbstractFilterTest   {

	@Override
	protected Application configure() {
		ResourceConfig config = new ResourceConfig();
		config.register(TestResource.class);
		PerfXFilterRegistry.registerContainerFilters(config);
		return config;

	}

	@Test
	public void ResourceAvailableTest() {		
		final Response response = target("testresource").request().get();
		assertThat(response.getStatus(), is(200));
	}

	@Test
	public void PerformanceLogTest() {		
		@SuppressWarnings("unused")
		final Response response = target("testresource").request().header(HTTP_HEADER_CORRELATION_ID, "1").get();
		
		assertThat(getField(Fields.RESPONSE_SIZE_B), is("4"));
		assertThat(getField(Fields.RESPONSE_TIME_MS), not(nullValue()));
		assertThat(getField(Fields.RESPONSE_STATUS), is(Integer.toString(TestResource.EXPECTED_STATUS_CODE)));
		assertThat(getField(Fields.RESPONSE_CONTENT_TYPE), is(TestResource.EXPECTED_CONTENT_TYPE));
		assertThat(getField(Fields.DIRECTION), is(Direction.IN.toString()));
		assertThat(getField(Fields.METHOD), is(TestResource.EXPECTED_REQUEST_METHOD));
		assertThat(getField(Fields.REMOTE_IP), not(nullValue()));
		assertThat(getField(Fields.REMOTE_HOST), not(nullValue()));
		assertThat(getField(Fields.REFERER), not(nullValue()));
		assertThat(getField(Fields.X_FORWARDED_FOR), not(nullValue()));
		assertThat(getField(Fields.LAYER), is(ContainerRequestContextAdapter.LAYER_NAME));
		
	}

	@Test
	public void ResponseTimeTest() {		
		@SuppressWarnings("unused")
		final Response response = target("testresource").request().delete();

		assertThat(new Double(getField(Fields.RESPONSE_TIME_MS)), greaterThan(TestResource.EXPECTED_REQUEST_TIME));
	}
}




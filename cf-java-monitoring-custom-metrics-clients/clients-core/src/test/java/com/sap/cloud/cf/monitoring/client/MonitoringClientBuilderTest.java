package com.sap.cloud.cf.monitoring.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gson.Gson;
import com.sap.cloud.cf.monitoring.client.MonitoringClientBuilder.MonitoringClientImpl;
import com.sap.cloud.cf.monitoring.client.configuration.ConfigurationProvider;
import com.sap.cloud.cf.monitoring.client.exceptions.MonitoringClientException;
import com.sap.cloud.cf.monitoring.client.model.Metric;
import com.sap.cloud.cf.monitoring.client.model.api.MetricRequest;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringClientBuilderTest {

    private static final int TEST_METRIC_VALUE = 30;
    private static final String TEST_METRIC_NAME = "test_metric_name";

    private static final String TEST_INSTANCE_GUID = UUID.randomUUID().toString();
    private static final String TEST_APPLICATION_GUID = UUID.randomUUID().toString();
    private static final int TEST_INSTANCE_INDEX = 0;
    private static final String TEST_MONITORING_URL = "https://localhost";
    private static final String TEST_APPLICATION_CLIENT_ID = "test_client_id";
    private static final String TEST_APPLICATION_CLIENT_SECRET = "test_client_secret";

    private static final Gson gson = new Gson();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Mock
    private ConfigurationProvider configurationProvider;

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse httpResponse;

    @Mock
    private StatusLine responseStatusLine;

    private MonitoringClient client;
    private Metric metric;

    @Before
    public void setUp() {
        when(configurationProvider.getApplicationGUID()).thenReturn(TEST_APPLICATION_GUID);
        when(configurationProvider.getInstanceGUID()).thenReturn(TEST_INSTANCE_GUID);
        when(configurationProvider.getInstanceIndex()).thenReturn(TEST_INSTANCE_INDEX);
        when(configurationProvider.getUrl()).thenReturn(TEST_MONITORING_URL);
        when(configurationProvider.getClientId()).thenReturn(TEST_APPLICATION_CLIENT_ID);
        when(configurationProvider.getClientSecret()).thenReturn(TEST_APPLICATION_CLIENT_SECRET.toCharArray());

        client = new MonitoringClientBuilder().setConfigurationProvider(configurationProvider)
            .setHttpClient(httpClient)
            .create();

        metric = new Metric(TEST_METRIC_NAME, TEST_METRIC_VALUE, System.currentTimeMillis());
        when(httpResponse.getStatusLine()).thenReturn(responseStatusLine);
    }

    @Test
    public void testSendNoMetric() {
        exception.expect(IllegalArgumentException.class);
        client.send((Metric) null);
    }

    @Test
    public void testSendNoMetrics() {
        exception.expect(IllegalArgumentException.class);
        client.send((List<Metric>) null);
    }

    @Test
    public void testSendEmptyMetrics() {
        exception.expect(IllegalArgumentException.class);
        client.send(new ArrayList<Metric>());
    }

    @Test
    public void testSend() throws Exception {
        ArgumentCaptor<HttpPost> createRequestCaptor = ArgumentCaptor.forClass(HttpPost.class);
        when(httpClient.execute(createRequestCaptor.capture())).thenReturn(httpResponse);
        when(responseStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_CREATED);

        client.send(metric);

        HttpPost actualPostRequest = createRequestCaptor.getValue();
        HttpEntity actualEntity = actualPostRequest.getEntity();
        String actualEntityString = EntityUtils.toString(actualEntity);
        MetricRequest actualMetricRequest = gson.fromJson(actualEntityString, MetricRequest.class);

        assertEquals(TEST_APPLICATION_GUID, actualMetricRequest.getApplicationGUID());
        assertEquals(TEST_INSTANCE_GUID, actualMetricRequest.getInstanceGUID());
        assertEquals(TEST_INSTANCE_INDEX, actualMetricRequest.getIndex());
        assertEquals(String.format(MonitoringClientImpl.REQUEST_URL_TEMPLATE, TEST_MONITORING_URL,
            TEST_APPLICATION_GUID, TEST_INSTANCE_GUID), actualPostRequest.getRequestLine().getUri());
        assertEquals("The request does not contain Authorization header", 1,
            actualPostRequest.getHeaders("Authorization").length);
    }

    @Test
    public void testSendUnexpectedCodeFromServer() throws Exception {
        exception.expect(MonitoringClientException.class);
        when(httpClient.execute(any(HttpPost.class))).thenReturn(httpResponse);
        when(responseStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        client.send(metric);
    }

    @Test
    public void testSendExceptionWhileExecuting() throws Exception {
        exception.expect(MonitoringClientException.class);
        doThrow(new IOException()).when(httpClient).execute(any(HttpPost.class));
        when(responseStatusLine.getStatusCode()).thenReturn(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        client.send(metric);
    }
}

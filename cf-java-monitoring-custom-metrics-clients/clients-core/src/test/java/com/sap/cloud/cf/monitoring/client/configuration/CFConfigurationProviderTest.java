package com.sap.cloud.cf.monitoring.client.configuration;

import static com.sap.cloud.cf.monitoring.client.configuration.CFConfigurationProvider.*;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.sap.cloud.cf.monitoring.client.configuration.CFConfigurationProvider;

public class CFConfigurationProviderTest {

    private static final String TEST_INSTANCE_GUID = UUID.randomUUID().toString();
    private static final String TEST_APPLICATION_GUID = UUID.randomUUID().toString();
    private static final String TEST_INSTANCE_INDEX = "0";
    private static final String TEST_MONITORING_URL = "https://localhost";
    private static final String TEST_CLIENT_ID = UUID.randomUUID().toString();
    private static final String TEST_SECRET = "super-secure-secret";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private Map<String, String> props;

    @Before
    public void setUp() {
        props = new HashMap<String, String>();
    }

    @Test
    public void testNoConfiguration() {
        exception.expect(IllegalArgumentException.class);
        new CFConfigurationProvider(null);
    }

    @Test
    public void testNoInstanceGuid() {
        exception.expect(IllegalArgumentException.class);
        new CFConfigurationProvider(props);
    }

    @Test
    public void testNoInstanceIndex() {
        exception.expect(IllegalArgumentException.class);
        props.put(CF_INSTANCE_GUID_KEY, TEST_INSTANCE_GUID);
        new CFConfigurationProvider(props);
    }

    @Test
    public void testNoVcapApplication() {
        exception.expect(IllegalArgumentException.class);
        props.put(CF_INSTANCE_GUID_KEY, TEST_INSTANCE_GUID);
        props.put(CF_INSTANCE_INDEX_KEY, TEST_INSTANCE_INDEX);
        new CFConfigurationProvider(props);
    }

    @Test
    public void testNoMonitoringURL() {
        exception.expect(IllegalArgumentException.class);
        props.put(CF_INSTANCE_GUID_KEY, TEST_INSTANCE_GUID);
        props.put(CF_INSTANCE_INDEX_KEY, TEST_INSTANCE_INDEX);
        props.put(VCAP_APPLICATION_KEY, "{}");
        new CFConfigurationProvider(props);
    }

    @Test
    public void testSuccessfulParsing() {
        props.put(CF_INSTANCE_GUID_KEY, TEST_INSTANCE_GUID);
        props.put(CF_INSTANCE_INDEX_KEY, TEST_INSTANCE_INDEX);
        props.put(VCAP_APPLICATION_KEY,
            "{\"application_id\":\"" + TEST_APPLICATION_GUID + "\",\"application_name\":\"monitoring-sample-app\"}");
        props.put(VCAP_SERVICES_KEY,
            "{\"application-logs\": [{\"credentials\": { \"" + METRICS_COLLECTOR_CLIENT_ID_KEY + "\" : \""
                    + TEST_CLIENT_ID + "\", \"" + METRICS_COLLECTOR_CLIENT_SECRET_KEY + "\": \"" + TEST_SECRET
                    + "\", \"" + METRICS_COLLECTOR_URL_KEY + "\": \""+ TEST_MONITORING_URL + "\"}}]}");
        CFConfigurationProvider provider = new CFConfigurationProvider(props);

        assertEquals(TEST_INSTANCE_GUID, provider.getInstanceGUID());
        assertEquals(TEST_APPLICATION_GUID, provider.getApplicationGUID());
        assertEquals(Integer.parseInt(TEST_INSTANCE_INDEX), provider.getInstanceIndex());
        assertEquals(TEST_MONITORING_URL, provider.getUrl());
        assertEquals(TEST_CLIENT_ID, provider.getClientId());
        assertEquals(TEST_SECRET, new String(provider.getClientSecret()));
    }
}

package com.sap.cloud.cf.monitoring.java;

import static com.sap.cloud.cf.monitoring.client.configuration.CFConfigurationProvider.METRICS_COLLECTOR_CLIENT_ID_KEY;
import static com.sap.cloud.cf.monitoring.client.configuration.CFConfigurationProvider.METRICS_COLLECTOR_CLIENT_SECRET_KEY;
import static com.sap.cloud.cf.monitoring.client.configuration.CFConfigurationProvider.METRICS_COLLECTOR_URL_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.codahale.metrics.MetricRegistry;
import com.sap.cloud.cf.monitoring.client.configuration.CFConfigurationProvider;
import com.sap.cloud.cf.monitoring.client.configuration.EnvUtils;

@RunWith(MockitoJUnitRunner.class)
public class CustomMetricRegistryTest {
    private static final String TEST_INSTANCE_GUID = UUID.randomUUID().toString();
    private static final String TEST_APPLICATION_GUID = UUID.randomUUID().toString();
    private static final String TEST_INSTANCE_INDEX = "0";
    private static final String TEST_MONITORING_URL = "https://localhost";
    private static final String TEST_CLIENT_ID = UUID.randomUUID().toString();
    private static final String TEST_SECRET = "super-secure-secret";

    private static final String[] CF_INSTANCE_GUID_KEY_ENV =
        { CFConfigurationProvider.CF_INSTANCE_GUID_KEY, TEST_INSTANCE_GUID };
    private static final String[] CF_INSTANCE_INDEX_KEY_ENV =
        { CFConfigurationProvider.CF_INSTANCE_INDEX_KEY, TEST_INSTANCE_INDEX };
    private static final String[] VCAP_APPLICATION_KEY_ENV = { CFConfigurationProvider.VCAP_APPLICATION_KEY,
            "{\"application_id\":\"" + TEST_APPLICATION_GUID + "\",\"application_name\":\"monitoring-sample-app\"}" };
    private static final String[] VCAP_SERVICES_KEY_ENV = { CFConfigurationProvider.VCAP_SERVICES_KEY,
            "{\"application-logs\": [{\"credentials\": { \"" + METRICS_COLLECTOR_CLIENT_ID_KEY + "\" : \""
                    + TEST_CLIENT_ID + "\", \"" + METRICS_COLLECTOR_CLIENT_SECRET_KEY + "\": \"" + TEST_SECRET
                    + "\", \"" + METRICS_COLLECTOR_URL_KEY + "\": \"" + TEST_MONITORING_URL + "\"}}]}" };

    @Before
    public void setUp() throws Exception {
        Field inst = CustomMetricRegistry.class.getDeclaredField("instance");
        inst.setAccessible(true);
        inst.set(null, null);
    }

    @Test
    public void testCustomMetricRegistryInitializeWithEnvs() throws Exception {
        EnvUtils.setEnvs(new String[][] { CF_INSTANCE_GUID_KEY_ENV, CF_INSTANCE_INDEX_KEY_ENV, VCAP_SERVICES_KEY_ENV,
                VCAP_APPLICATION_KEY_ENV });
        CustomMetricRegistry metricRegistry = (CustomMetricRegistry) CustomMetricRegistry.get();

        assertNotNull(metricRegistry.getReporter());
    }

    @Test
    public void testCustomMetricRegistryInitializeWithoutEnvs() throws Exception {
        EnvUtils.setEnvs(new String[][] {});
        MetricRegistry metricRegistry = CustomMetricRegistry.get();
        MetricRegistry metricRegistry1 = CustomMetricRegistry.get();

        assertTrue(metricRegistry instanceof CustomMetricRegistry);
        assertEquals(metricRegistry, metricRegistry1);
        assertNull(((CustomMetricRegistry) metricRegistry).getReporter());
    }

    @Test
    public void testCustomMetricRegistryInitializeWithEnvsAndDisabledFlag() throws Exception {
        EnvUtils.setEnvs(new String[][] { CF_INSTANCE_GUID_KEY_ENV, CF_INSTANCE_INDEX_KEY_ENV, VCAP_SERVICES_KEY_ENV,
                VCAP_APPLICATION_KEY_ENV, getCustomMetricsEnv(false) });
        MetricRegistry metricRegistry = CustomMetricRegistry.get();

        assertNull(((CustomMetricRegistry) metricRegistry).getReporter());
    }

    @Test
    public void testCustomMetricRegistryInitializeWithEnvsAndEnabledFlag() throws Exception {
        EnvUtils.setEnvs(new String[][] { CF_INSTANCE_GUID_KEY_ENV, CF_INSTANCE_INDEX_KEY_ENV, VCAP_SERVICES_KEY_ENV,
                VCAP_APPLICATION_KEY_ENV, getCustomMetricsEnv(true) });
        MetricRegistry metricRegistry = CustomMetricRegistry.get();

        assertNotNull(((CustomMetricRegistry) metricRegistry).getReporter());
    }

    private static String[] getCustomMetricsEnv(boolean isEnable) {
        return new String[] { "CUSTOM_METRICS", "{\n" + //
                "    \"interval\": \"20000\",\n" + //
                "    \"enabled\": \"" + isEnable + "\",\n" + //
                "    \"metrics\": [\"timer\", \"summary\"]\n" + //
                "}" };
    }
}
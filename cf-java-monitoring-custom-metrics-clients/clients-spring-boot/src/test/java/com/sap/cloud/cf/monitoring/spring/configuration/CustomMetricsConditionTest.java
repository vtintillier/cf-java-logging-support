package com.sap.cloud.cf.monitoring.spring.configuration;

import static com.sap.cloud.cf.monitoring.client.configuration.CFConfigurationProvider.METRICS_COLLECTOR_CLIENT_ID_KEY;
import static com.sap.cloud.cf.monitoring.client.configuration.CFConfigurationProvider.METRICS_COLLECTOR_CLIENT_SECRET_KEY;
import static com.sap.cloud.cf.monitoring.client.configuration.CFConfigurationProvider.METRICS_COLLECTOR_URL_KEY;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.sap.cloud.cf.monitoring.client.configuration.CFConfigurationProvider;

public class CustomMetricsConditionTest {
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

    private CustomMetricsCondition condition;

    @Before
    public void setup() {
        condition = new CustomMetricsCondition();
    }

    @Test
    public void testMatches_WithAllEnvs() throws Exception {
        EnvUtils.setEnvs(new String[][] { CF_INSTANCE_GUID_KEY_ENV, CF_INSTANCE_INDEX_KEY_ENV, VCAP_SERVICES_KEY_ENV,
                VCAP_APPLICATION_KEY_ENV });

        boolean matches = condition.matches(null, null);

        assertTrue(matches);
    }

    @Test
    public void testMatches_Without_VCAP_APPLICATION_KEY() throws Exception {
        EnvUtils.setEnvs(new String[][] { CF_INSTANCE_GUID_KEY_ENV, CF_INSTANCE_INDEX_KEY_ENV, VCAP_SERVICES_KEY_ENV });
        boolean matches = condition.matches(null, null);

        assertFalse(matches);
    }

    @Test
    public void testMatches_Without_VCAP_SERVICES_KEY_ENV() throws Exception {
        EnvUtils
            .setEnvs(new String[][] { CF_INSTANCE_GUID_KEY_ENV, CF_INSTANCE_INDEX_KEY_ENV, VCAP_APPLICATION_KEY_ENV });

        boolean matches = condition.matches(null, null);

        assertFalse(matches);
    }

    @Test
    public void testMatches_Without_CF_INSTANCE_INDEX_KEY_ENV() throws Exception {
        EnvUtils.setEnvs(new String[][] { CF_INSTANCE_GUID_KEY_ENV, VCAP_SERVICES_KEY_ENV, VCAP_APPLICATION_KEY_ENV });

        boolean matches = condition.matches(null, null);

        assertFalse(matches);
    }

    @Test
    public void testMatches_Without_CF_INSTANCE_GUID_KEY_ENV() throws Exception {
        EnvUtils.setEnvs(new String[][] { CF_INSTANCE_INDEX_KEY_ENV, VCAP_SERVICES_KEY_ENV, VCAP_APPLICATION_KEY_ENV });

        boolean matches = condition.matches(null, null);

        assertFalse(matches);
    }

    @Test
    public void testMatches_WithoutAllEnvs() throws Exception {
        EnvUtils.setEnvs(new String[][] {});
        boolean matches = condition.matches(null, null);

        assertFalse(matches);
    }
}

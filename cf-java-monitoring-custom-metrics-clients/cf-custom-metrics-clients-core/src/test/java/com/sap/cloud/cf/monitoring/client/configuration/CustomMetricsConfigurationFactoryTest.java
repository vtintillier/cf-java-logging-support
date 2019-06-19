package com.sap.cloud.cf.monitoring.client.configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class CustomMetricsConfigurationFactoryTest {

    @Test
    public void testMatches_WithoutEnv() throws Exception {
        EnvUtils.setEnvs(new String[][] {});

        testDefault();
    }

    @Test
    public void testMatches_WithEmptyEnv() throws Exception {
        String[] CUSTOM_METRICS_ENV = new String[] { "CUSTOM_METRICS", "" };
        EnvUtils.setEnvs(new String[][] { CUSTOM_METRICS_ENV });

        testDefault();
    }

    private void testDefault() {
        CustomMetricsConfiguration config = CustomMetricsConfigurationFactory.create();

        assertTrue(config.isEnabled());
        assertEquals(CustomMetricsConfiguration.DEFAULT_INTERVAL, config.getInterval());
        assertNotNull(config.getMetrics());
        assertTrue(config.getMetrics().isEmpty());
        assertFalse(config.metricQuantiles());
    }

    @Test
    public void testMatches_WithEnv() throws Exception {
        EnvUtils.setEnvs(new String[][] { getCustomMetricsEnv("20000") });

        CustomMetricsConfiguration config = CustomMetricsConfigurationFactory.create();

        assertFalse(config.isEnabled());
        assertEquals(20_000, config.getInterval());
        List<String> metrics = config.getMetrics();
        assertEquals(2, metrics.size());
        assertTrue(metrics.contains("timer"));
        assertTrue(metrics.contains("summary"));
        assertTrue(config.metricQuantiles());
    }

    @Test
    public void testMatches_WithShortIntervalEnv() throws Exception {
        EnvUtils.setEnvs(new String[][] { getCustomMetricsEnv("1000") });

        CustomMetricsConfiguration config = CustomMetricsConfigurationFactory.create();

        assertFalse(config.isEnabled());
        assertEquals(CustomMetricsConfiguration.DEFAULT_INTERVAL, config.getInterval());
        List<String> metrics = config.getMetrics();
        assertEquals(2, metrics.size());
        assertTrue(metrics.contains("timer"));
        assertTrue(metrics.contains("summary"));
    }

    @Test(expected = NumberFormatException.class)
    public void testMatches_WrongIntervalFormat() throws Exception {
        EnvUtils.setEnvs(new String[][] { getCustomMetricsEnv("wronginterval") });

        CustomMetricsConfigurationFactory.create();
    }

    private static String[] getCustomMetricsEnv(String interval) {
        return new String[] { "CUSTOM_METRICS", getJson(interval) };
    }

    private static String getJson(String interval) {
        return "{\n" + //
                "    \"interval\": \"" + interval + "\",\n" + //
                "    \"enabled\": \"false\",\n" + //
                "    \"metrics\": [\"timer\", \"summary\"],\n" + //
                "    \"metricQuantiles\": \"true\"\n" + //
                "}";
    }
}

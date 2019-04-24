package com.sap.cloud.cf.monitoring.spring.configuration;

import static org.junit.Assert.*;

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
        assertEquals(60 * 1000, config.getInterval());
        assertNull(config.getMetrics());
    }

    @Test
    public void testMatches_WithEnv() throws Exception {
        EnvUtils.setEnvs(new String[][] { getCustomMetricsEnv() });

        CustomMetricsConfiguration config = CustomMetricsConfigurationFactory.create();

        System.out.println(config);

        assertFalse(config.isEnabled());
        assertEquals(1000, config.getInterval());
        List<String> metrics = config.getMetrics();
        assertEquals(2, metrics.size());
        assertTrue(metrics.contains("timer"));
        assertTrue(metrics.contains("summary"));
    }

    public static String[] getCustomMetricsEnv() {
        return new String[] { "CUSTOM_METRICS", getJson() };
    }

    private static String getJson() {
        return "{\n" + //
                "    \"interval\": \"1000\",\n" + //
                "    \"enabled\": \"false\",\n" + //
                "    \"metrics\": [\"timer\", \"summary\"]\n" + //
                "}";
    }

}

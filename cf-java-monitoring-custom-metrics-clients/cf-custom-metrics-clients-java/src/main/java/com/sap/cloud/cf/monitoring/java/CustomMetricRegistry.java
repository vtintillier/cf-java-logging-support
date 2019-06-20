package com.sap.cloud.cf.monitoring.java;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.sap.cloud.cf.monitoring.client.MonitoringClient;
import com.sap.cloud.cf.monitoring.client.MonitoringClientBuilder;
import com.sap.cloud.cf.monitoring.client.configuration.CFConfigurationProvider;
import com.sap.cloud.cf.monitoring.client.configuration.ConfigurationProvider;
import com.sap.cloud.cf.monitoring.client.configuration.CustomMetricsConfiguration;
import com.sap.cloud.cf.monitoring.client.configuration.CustomMetricsConfigurationFactory;

public class CustomMetricRegistry extends MetricRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomMetricRegistry.class);
    private static volatile CustomMetricRegistry instance = null;

    private ConfigurationProvider configProvider;
    private CustomMetricsConfiguration customMetricsConfig;
    private CustomMetricsReporter reporter;

    public static MetricRegistry get() {
        if (instance == null) {
            synchronized (CustomMetricRegistry.class) {
                if (instance == null) {
                    instance = new CustomMetricRegistry();
                }
            }
        }
        return instance;
    }

    protected CustomMetricsReporter getReporter() {
        return reporter;
    }

    private CustomMetricRegistry() {
        configProvider = initConfigProvider();
        customMetricsConfig = CustomMetricsConfigurationFactory.create();
        initializeAndStartReporter();
    }

    private static CFConfigurationProvider initConfigProvider() {
        try {
            return new CFConfigurationProvider();
        } catch (IllegalArgumentException e) {
            LOGGER.error("Required ENVs are missing: {}", e);
            return null;
        }
    }

    private void initializeAndStartReporter() {
        if (configProvider == null || customMetricsConfig == null || !customMetricsConfig.isEnabled()) {
            LOGGER.error("Custom Metrics reporter will not start since required ENVs are missing or environment variable 'enable' is false.");
            return;
        }
        MonitoringClient client = new MonitoringClientBuilder().setConfigurationProvider(configProvider).create();
        reporter = new CustomMetricsReporter(this, client, customMetricsConfig);
        reporter.start(customMetricsConfig.getInterval(), TimeUnit.MILLISECONDS);
    }
}

package com.sap.cloud.cf.monitoring.spring;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.cf.monitoring.client.MonitoringClient;
import com.sap.cloud.cf.monitoring.client.configuration.CustomMetricsConfiguration;
import com.sap.cloud.cf.monitoring.client.exceptions.MonitoringClientException;
import com.sap.cloud.cf.monitoring.client.model.Metric;
import com.sap.cloud.cf.monitoring.spring.configuration.metrics.DefaultMetricsConverter;
import com.sap.cloud.cf.monitoring.spring.configuration.metrics.MetricsConverterFactory;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.step.StepMeterRegistry;
import io.micrometer.core.instrument.step.StepRegistryConfig;

public class CustomMetricWriter extends StepMeterRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(CustomMetricWriter.class);
    private static final int SEND_METRICS_RETRIES = 2;

    private final MonitoringClient client;
    private final CustomMetricsConfiguration config;

    public CustomMetricWriter(CustomMetricsConfiguration config, Clock clock, MonitoringClient client) {
        super(new MicrometerMetricWriterConfig(config), clock);
        this.config = config;
        this.client = client;
        if (!config.isEnabled()) {
            LOG.info("Custom Metric reporting is disabled");
            return;
        }
        LOG.info("Starting custom metrics reporting with the following configuration: {}", config);
        start();
    }

    @Override
    protected void publish() {
        try {
            List<Metric> metrics = getMeters().stream().flatMap(meter -> {
                return MetricsConverterFactory.getMetrics(meter);
            }).filter(Objects::nonNull).collect(Collectors.toList());

            List<String> whitelistedMetricNames = config.getMetrics();
            if (whitelistedMetricNames != null && !whitelistedMetricNames.isEmpty()) {
                LOG.debug("Applying filter for whitelisted metrics {}", whitelistedMetricNames);
                sendMetrics(getWhiteListedMetrics(metrics, whitelistedMetricNames));
                return;
            }
            sendMetrics(metrics);
        } catch (Exception e) { //NOSONAR
            LOG.error("Unable to send metrics to the Monitoring Service", e);
        }
    }

    private void sendMetrics(List<Metric> metrics) {
        for (int i = 0; i < SEND_METRICS_RETRIES; i++) {
            try {
                client.send(metrics);
                break;
            } catch (MonitoringClientException e) {
                LOG.error("Unable to send metrics to the Monitoring Service retrying", e);
            }
        }
    }

    private List<Metric> getWhiteListedMetrics(List<Metric> metrics, List<String> whitelistedMetricNames) {
        List<Metric> whitelistedMetrics = metrics.stream() //
            .filter(metric -> whitelistedMetricNames.contains(metric.getName())) //
            .collect(Collectors.toList());

        return whitelistedMetrics;
    }

    @Override
    protected TimeUnit getBaseTimeUnit() {
        return DefaultMetricsConverter.getBaseTimeUnit();
    }

    private static final class MicrometerMetricWriterConfig implements StepRegistryConfig {

        private final CustomMetricsConfiguration config;

        public MicrometerMetricWriterConfig(CustomMetricsConfiguration config) {
            this.config = config;
        }

        @Override
        public String get(String arg0) {
            return null;
        }

        @Override
        public String prefix() {
            return "";
        }

        @Override
        public boolean enabled() {
            return config.isEnabled();
        }

        @Override
        public Duration step() {
            return Duration.ofMillis(config.getInterval());
        }
    }

}

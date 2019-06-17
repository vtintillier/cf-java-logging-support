package com.sap.cloud.cf.monitoring.java;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;
import com.sap.cloud.cf.monitoring.client.MonitoringClient;
import com.sap.cloud.cf.monitoring.client.configuration.CustomMetricsConfiguration;
import com.sap.cloud.cf.monitoring.client.exceptions.MonitoringClientException;
import com.sap.cloud.cf.monitoring.client.model.Metric;
import com.sap.cloud.cf.monitoring.java.converter.CounterConverter;
import com.sap.cloud.cf.monitoring.java.converter.GaugeConverter;
import com.sap.cloud.cf.monitoring.java.converter.HistogramConverter;
import com.sap.cloud.cf.monitoring.java.converter.MeterConverter;
import com.sap.cloud.cf.monitoring.java.converter.TimerConverter;

public class CustomMetricsReporter extends ScheduledReporter {
    static final int SEND_METRICS_ATTEMPTS = 2;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomMetricsReporter.class);
    private final MonitoringClient client;
    private final CustomMetricsConfiguration customMetricsConfig;

    private static MetricFilter getFilter(final List<String> whitelistMetrics) {
        if (whitelistMetrics == null || whitelistMetrics.isEmpty()) {
            return MetricFilter.ALL;
        }
        return new MetricFilter() {
            @Override
            public boolean matches(String name, com.codahale.metrics.Metric metric) {
                return whitelistMetrics.contains(name);
            }
        };
    }

    public CustomMetricsReporter(MetricRegistry registry, MonitoringClient client,
            CustomMetricsConfiguration customMetricsConfig) {
        super(registry, "custom-metrics-reporter", getFilter(customMetricsConfig.getMetrics()), TimeUnit.SECONDS,
                TimeUnit.MILLISECONDS);
        this.client = client;
        this.customMetricsConfig = customMetricsConfig;
    }

    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
            SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
        try {
            List<Metric> convertedMetrics = convert(gauges, counters, histograms, meters, timers);
            if (convertedMetrics.isEmpty()) {
                LOGGER.debug("No metrics for sending.");
                return;
            }
            sendMetrics(convertedMetrics);
        } catch (Exception e) {
            LOGGER.error("Unable to send metrics.", e);
        }
    }

    private List<Metric> convert(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
            SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
        List<Metric> result = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        boolean metricsAggregation = customMetricsConfig.isMetricsAggregation();

        result.addAll(new GaugeConverter().convert(gauges, timestamp));
        result.addAll(new CounterConverter().convert(counters, timestamp));
        result.addAll(new HistogramConverter(metricsAggregation).convert(histograms, timestamp));
        result.addAll(new MeterConverter(metricsAggregation).convert(meters, timestamp));
        result.addAll(new TimerConverter(metricsAggregation).convert(timers, timestamp));

        return result;
    }

    private void sendMetrics(List<Metric> convertedMetrics) {
        for (int i = 0; i < SEND_METRICS_ATTEMPTS; i++) {
            try {
                client.send(convertedMetrics);
                break;
            } catch (MonitoringClientException e) {
                LOGGER.error("Unable to send metrics. Retrying ...", e);
            }
        }
    }
}

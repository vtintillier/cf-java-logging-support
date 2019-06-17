package com.sap.cloud.cf.monitoring.client.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CustomMetricsConfiguration {

    static final long DEFAULT_INTERVAL = TimeUnit.MINUTES.toMillis(1);
    private long interval = DEFAULT_INTERVAL;
    private boolean enabled = true;
    private List<String> metrics;
    private boolean metricsAggregation = false;

    public long getInterval() {
        return interval;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getMetrics() {
        if (this.metrics == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(metrics);
    }

    public boolean isMetricsAggregation() {
        return metricsAggregation;
    }

    @Override
    public String toString() {
        return new StringBuilder("CustomMetricsConfiguration[").append("interval=")
            .append(interval)
            .append(", enabled=")
            .append(enabled)
            .append(", metrics=")
            .append(metrics)
            .append("]")
            .append(", metricsAggregation=")
            .append(metricsAggregation)
            .toString();
    }
}

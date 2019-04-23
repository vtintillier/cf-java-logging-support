package com.sap.cloud.cf.monitoring.spring.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CustomMetricsConfiguration {

    private long interval = TimeUnit.MINUTES.toMillis(1);
    private boolean enabled = true;
    private List<String> metrics;

    public long getInterval() {
        return interval;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getMetrics() {
        if (this.metrics == null) {
            return null;
        }
        return new ArrayList<String>(metrics);
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
            .toString();
    }
}

package com.sap.cloud.cf.monitoring.spring.configuration.metrics;

import java.util.stream.Stream;

import com.sap.cloud.cf.monitoring.client.model.Metric;

import io.micrometer.core.instrument.*;

public class MetricsConverterFactory {
    public static Stream<Metric> getMetrics(Meter meter) {
        if (meter instanceof DistributionSummary) {
            return new DistributionSummaryMetricsConverter().getMetrics((DistributionSummary) meter);
        } else if (meter instanceof FunctionTimer) {
            return new FunctionTimerMetricsConverter().getMetrics((FunctionTimer) meter);
        } else if (meter instanceof Timer) {
            return new TimerMetricsConverter().getMetrics((Timer) meter);
        } else {
            return new DefaultMetricsConverter().getMetrics(meter);
        }
    }
}

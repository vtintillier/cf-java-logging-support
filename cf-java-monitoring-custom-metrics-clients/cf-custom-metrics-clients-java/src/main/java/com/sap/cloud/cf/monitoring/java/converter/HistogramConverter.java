package com.sap.cloud.cf.monitoring.java.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Snapshot;
import com.sap.cloud.cf.monitoring.client.model.Metric;

public final class HistogramConverter extends MetricConverter<Histogram> {

    private final boolean metricsAggregation;

    public HistogramConverter(boolean metricsAggregation) {
        this.metricsAggregation = metricsAggregation;
    }

    @Override
    List<Metric> convertMetricEntry(Entry<String, Histogram> metricEntry, long timestamp) {
        ArrayList<Metric> result = new ArrayList<>();
        Histogram histogram = metricEntry.getValue();
        Snapshot snapshot = histogram.getSnapshot();
        String key = metricEntry.getKey();
        MetricType type = MetricType.HISTOGRAM;

        result.add(buildCustomMetric(key + ".count", histogram.getCount(), type, timestamp));
        result.add(buildCustomMetric(key + ".max", snapshot.getMax(), type, timestamp));
        result.add(buildCustomMetric(key + ".min", snapshot.getMin(), type, timestamp));
        result.add(buildCustomMetric(key + ".p50", snapshot.getMedian(), type, timestamp));
        result.add(buildCustomMetric(key + ".p95", snapshot.get95thPercentile(), type, timestamp));
        result.add(buildCustomMetric(key + ".p99", snapshot.get99thPercentile(), type, timestamp));

        if (metricsAggregation) {
            result.add(buildCustomMetric(key + ".mean", snapshot.getMean(), type, timestamp));
            result.add(buildCustomMetric(key + ".p75", snapshot.get75thPercentile(), type, timestamp));
            result.add(buildCustomMetric(key + ".p98", snapshot.get98thPercentile(), type, timestamp));
            result.add(buildCustomMetric(key + ".p999", snapshot.get999thPercentile(), type, timestamp));
            result.add(buildCustomMetric(key + ".stddev", snapshot.getStdDev(), type, timestamp));
        }

        return result;
    }
}
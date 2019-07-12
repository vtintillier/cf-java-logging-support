package com.sap.cloud.cf.monitoring.java.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.sap.cloud.cf.monitoring.client.model.Metric;

public abstract class MetricConverter<T> {
    private static final String KEY_TYPE = "type";

    public enum MetricType {
                            TIMER("timer"), HISTOGRAM("histogram"), GAUGE("gauge"), METER("meter"), COUNTER("counter");

        private final String metricTypeName;

        String getMetricTypeName() {
            return metricTypeName;
        }

        private MetricType(String metricTypeName) {
            this.metricTypeName = metricTypeName;
        }
    }

    public List<Metric> convert(Map<String, T> metrics, long timestamp) {
        return metrics.entrySet()
                .stream()
                .map(entry -> convertMetricEntry(entry, timestamp))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    protected abstract List<Metric> convertMetricEntry(Entry<String, T> metricEntry, long timestamp);

    protected Metric buildCustomMetric(String name, double value, MetricType type, long timestamp) {
        Map<String, String> tags = new HashMap<>();
        tags.put(KEY_TYPE, type.getMetricTypeName());
        return new Metric(name, value, timestamp, tags);
    }
}
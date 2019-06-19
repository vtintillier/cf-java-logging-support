package com.sap.cloud.cf.monitoring.java.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sap.cloud.cf.monitoring.client.model.Metric;

public abstract class MetricConverter<T> {
    private static final String KEY_TYPE = "type";

    enum MetricType {
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
        ArrayList<Metric> result = new ArrayList<>();
        for (Entry<String, T> entry: metrics.entrySet()) {
            result.addAll(convertMetricEntry(entry, timestamp));
        }
        return result;
    }

    abstract List<Metric> convertMetricEntry(Entry<String, T> metricEntry, long timestamp);

    Metric buildCustomMetric(String name, double value, MetricType type, long timestamp) {
        Map<String, String> tags = new HashMap<>();
        tags.put(KEY_TYPE, type.getMetricTypeName());
        return new Metric(name, value, timestamp, tags);
    }
}

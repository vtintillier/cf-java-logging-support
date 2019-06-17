package com.sap.cloud.cf.monitoring.java.converter;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import com.codahale.metrics.Counter;
import com.sap.cloud.cf.monitoring.client.model.Metric;

public final class CounterConverter extends MetricConverter<Counter> {

    @Override
    List<Metric> convertMetricEntry(Entry<String, Counter> metricEntry, long timestamp) {
        Counter counter = metricEntry.getValue();
        return Arrays.asList(
            buildCustomMetric(metricEntry.getKey() + ".count", counter.getCount(), MetricType.COUNTER, timestamp));
    }
}
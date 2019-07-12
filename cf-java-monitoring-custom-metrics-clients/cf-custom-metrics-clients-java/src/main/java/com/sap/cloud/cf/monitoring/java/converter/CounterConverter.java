package com.sap.cloud.cf.monitoring.java.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.codahale.metrics.Counter;
import com.sap.cloud.cf.monitoring.client.model.Metric;

public final class CounterConverter extends MetricConverter<Counter> {

    @Override
    protected List<Metric> convertMetricEntry(Entry<String, Counter> metricEntry, long timestamp) {
        List<Metric> result = new ArrayList<>();
        Counter counter = metricEntry.getValue();
        result.add(buildCustomMetric(metricEntry.getKey() + ".count", counter.getCount(), MetricType.COUNTER, timestamp));

        return result;
    }
}

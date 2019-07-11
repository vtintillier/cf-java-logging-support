package com.sap.cloud.cf.monitoring.java.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.codahale.metrics.Gauge;
import com.sap.cloud.cf.monitoring.client.model.Metric;

public final class GaugeConverter extends MetricConverter<Gauge> {

    @Override
    protected List<Metric> convertMetricEntry(Entry<String, Gauge> metricEntry, long timestamp) {
        List<Metric> result = new ArrayList<>();
        Object gaugeValue = metricEntry.getValue().getValue();
        if (gaugeValue instanceof Number) {
            Number number = (Number) gaugeValue;
            result.add(buildCustomMetric(metricEntry.getKey() + ".value", number.doubleValue(), MetricType.GAUGE,
                                         timestamp));
        } else {
            throw new IllegalArgumentException(String.format("The type {%s} for Gauge {%s} is invalid. The supported type is {%s}", gaugeValue.getClass().getName(), metricEntry.getKey(), Number.class.getName()));
        }
        return result;
    }
}

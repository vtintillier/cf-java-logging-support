package com.sap.cloud.cf.monitoring.java.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.codahale.metrics.Meter;
import com.sap.cloud.cf.monitoring.client.model.Metric;

public final class MeterConverter extends MetricConverter<Meter> {

    private final boolean metricsAggregation;

    public MeterConverter(boolean metricsAggregation) {
        this.metricsAggregation = metricsAggregation;
    }

    @Override
    List<Metric> convertMetricEntry(Entry<String, Meter> metricEntry, long timestamp) {
        ArrayList<Metric> result = new ArrayList<>();
        Meter meter = metricEntry.getValue();
        String key = metricEntry.getKey();
        MetricType type = MetricType.METER;

        result.add(buildCustomMetric(key + ".count", meter.getCount(), type, timestamp));
        result.add(buildCustomMetric(key + ".m1_rate", meter.getOneMinuteRate(), type, timestamp));

        if (metricsAggregation) {
            result.add(buildCustomMetric(key + ".mean_rate", meter.getMeanRate(), type, timestamp));
            result.add(buildCustomMetric(key + ".m5_rate", meter.getFiveMinuteRate(), type, timestamp));
            result.add(buildCustomMetric(key + ".m15_rate", meter.getFifteenMinuteRate(), type, timestamp));
        }

        return result;
    }
}
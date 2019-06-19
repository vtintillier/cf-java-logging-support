package com.sap.cloud.cf.monitoring.java.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.sap.cloud.cf.monitoring.client.model.Metric;

public final class TimerConverter extends MetricConverter<Timer> {

    private final boolean metricQuantiles;

    public TimerConverter(boolean metricQuantiles) {
        this.metricQuantiles = metricQuantiles;
    }

    @Override
    List<Metric> convertMetricEntry(Entry<String, Timer> metricEntry, long timestamp) {
        ArrayList<Metric> result = new ArrayList<>();
        Timer timer = metricEntry.getValue();
        Snapshot snapshot = timer.getSnapshot();
        String key = metricEntry.getKey();
        MetricType type = MetricType.TIMER;

        result.add(buildCustomMetric(key + ".count", timer.getCount(), type, timestamp));
        result.add(buildCustomMetric(key + ".max", snapshot.getMax(), type, timestamp));
        result.add(buildCustomMetric(key + ".min", snapshot.getMin(), type, timestamp));
        result.add(buildCustomMetric(key + ".p50", snapshot.getMedian(), type, timestamp));
        result.add(buildCustomMetric(key + ".p95", snapshot.get95thPercentile(), type, timestamp));
        result.add(buildCustomMetric(key + ".p99", snapshot.get99thPercentile(), type, timestamp));
        result.add(buildCustomMetric(key + ".m1_rate", timer.getOneMinuteRate(), type, timestamp));

        if (metricQuantiles) {
            result.add(buildCustomMetric(key + ".mean", snapshot.getMean(), type, timestamp));
            result.add(buildCustomMetric(key + ".p75", snapshot.get75thPercentile(), type, timestamp));
            result.add(buildCustomMetric(key + ".p98", snapshot.get98thPercentile(), type, timestamp));
            result.add(buildCustomMetric(key + ".p999", snapshot.get999thPercentile(), type, timestamp));
            result.add(buildCustomMetric(key + ".stddev", snapshot.getStdDev(), type, timestamp));
            result.add(buildCustomMetric(key + ".m15_rate", timer.getFifteenMinuteRate(), type, timestamp));
            result.add(buildCustomMetric(key + ".m5_rate", timer.getFiveMinuteRate(), type, timestamp));
            result.add(buildCustomMetric(key + ".mean_rate", timer.getMeanRate(), type, timestamp));
        }
        return result;
    }
}

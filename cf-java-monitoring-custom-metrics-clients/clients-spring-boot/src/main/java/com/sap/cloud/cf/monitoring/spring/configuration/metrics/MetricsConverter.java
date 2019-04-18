package com.sap.cloud.cf.monitoring.spring.configuration.metrics;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sap.cloud.cf.monitoring.client.model.Metric;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;

public abstract class MetricsConverter<T extends Meter> {
    private static final DecimalFormat PERCENTILE_FORMAT = new DecimalFormat("#.####");

    abstract public Stream<Metric> getMetrics(T meter);

    protected Stream<Metric> getMetrics(Meter meter, HistogramSnapshot snapshot) {
        return Stream.concat(
            Stream.of(toMetric(withStatistic(meter, "count"), snapshot.count()),
                toMetric(withStatistic(meter, "max"), snapshot.max(getBaseTimeUnit())),
                toMetric(withStatistic(meter, "mean"), snapshot.mean(getBaseTimeUnit())),
                toMetric(withStatistic(meter, "totalTime"), snapshot.total(getBaseTimeUnit()))),
            getMetrics(meter, snapshot.percentileValues()));
    }

    protected Stream<Metric> getMetrics(Meter meter, ValueAtPercentile[] percentiles) {
        return Arrays.stream(percentiles)
            .map(percentile -> toMetric(withPercentile(meter, percentile), percentile.value(getBaseTimeUnit())));
    }

    protected Metric toMetric(Meter.Id id, double value) {
        if (Double.isNaN(value)) {
            return null;
        }

        Map<String, String> tags = id.getTags().stream().collect(Collectors.toMap(Tag::getKey, Tag::getValue));
        return new Metric(id.getName(), value, System.currentTimeMillis(), tags);
    }

    protected Meter.Id withPercentile(Meter meter, ValueAtPercentile percentile) {
        return withStatistic(meter,
            String.format("%spercentile", PERCENTILE_FORMAT.format(percentile.percentile() * 100)));
    }

    protected Meter.Id withStatistic(Meter meter, String type) {
        return meter.getId().withTag(Tag.of("statistic", type));
    }

    public static TimeUnit getBaseTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }
}

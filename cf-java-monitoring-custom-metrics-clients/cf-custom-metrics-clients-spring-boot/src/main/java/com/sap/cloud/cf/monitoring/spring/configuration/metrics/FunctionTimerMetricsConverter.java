package com.sap.cloud.cf.monitoring.spring.configuration.metrics;

import java.util.stream.Stream;

import com.sap.cloud.cf.monitoring.client.model.Metric;

import io.micrometer.core.instrument.FunctionTimer;

public class FunctionTimerMetricsConverter extends MetricsConverter<FunctionTimer> {

    @Override
    public Stream<Metric> getMetrics(FunctionTimer meter) {
        return Stream.of(toMetric(withStatistic(meter, "count"), meter.count()),
            toMetric(withStatistic(meter, "mean"), meter.mean(getBaseTimeUnit())),
            toMetric(withStatistic(meter, "totalTime"), meter.totalTime(getBaseTimeUnit())));
    }

}

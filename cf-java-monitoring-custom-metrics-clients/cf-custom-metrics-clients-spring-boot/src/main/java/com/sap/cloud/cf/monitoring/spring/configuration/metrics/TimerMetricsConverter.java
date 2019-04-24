package com.sap.cloud.cf.monitoring.spring.configuration.metrics;

import java.util.stream.Stream;

import com.sap.cloud.cf.monitoring.client.model.Metric;

import io.micrometer.core.instrument.Timer;

public class TimerMetricsConverter extends MetricsConverter<Timer> {

    @Override
    public Stream<Metric> getMetrics(Timer meter) {
        return getMetrics(meter, meter.takeSnapshot());
    }

}

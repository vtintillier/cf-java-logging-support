package com.sap.cloud.cf.monitoring.spring.configuration.metrics;

import static java.util.stream.StreamSupport.stream;

import java.util.stream.Stream;

import com.sap.cloud.cf.monitoring.client.model.Metric;

import io.micrometer.core.instrument.Meter;

public class DefaultMetricsConverter extends MetricsConverter<Meter> {

    @Override
    public Stream<Metric> getMetrics(Meter meter) {
        return stream(meter.measure().spliterator(), false)
            .map(measurement -> toMetric(meter.getId().withTag(measurement.getStatistic()), measurement.getValue()));
    }

}

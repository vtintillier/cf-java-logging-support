package com.sap.cloud.cf.monitoring.spring.configuration.metrics;

import java.util.stream.Stream;

import com.sap.cloud.cf.monitoring.client.model.Metric;

import io.micrometer.core.instrument.DistributionSummary;

public class DistributionSummaryMetricsConverter extends MetricsConverter<DistributionSummary> {

    @Override
    public Stream<Metric> getMetrics(DistributionSummary meter) {
        return getMetrics(meter, meter.takeSnapshot());
    }

}

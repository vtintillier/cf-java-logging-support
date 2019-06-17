package com.sap.cloud.cf.monitoring.java.converter;

import static com.sap.cloud.cf.monitoring.java.converter.ConverterTestUtil.SUFFIX_VALUE;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Test;

import com.codahale.metrics.Gauge;
import com.sap.cloud.cf.monitoring.client.model.Metric;
import com.sap.cloud.cf.monitoring.java.converter.MetricConverter.MetricType;

@SuppressWarnings("rawtypes")
public class GaugeConverterTest {

    private static final String GAUGE_METRIC = "gaugeMetric";
    private static final Double METRIC_VALUE = 5.00;
    private final long currentTimeMillis = System.currentTimeMillis();

    @Test
    public void testGaugeMetric() {
        SortedMap<String, Gauge> gauges = new TreeMap<>();
        Gauge<Number> gauge = new Gauge<Number>() {

            @Override
            public Number getValue() {
                return new Double(METRIC_VALUE);
            }

        };
        gauges.put(GAUGE_METRIC, gauge);
        List<Metric> metrics = new GaugeConverter().convert(gauges, currentTimeMillis);
        ConverterTestUtil util =
            new ConverterTestUtil(metrics, GAUGE_METRIC, MetricType.GAUGE.getMetricTypeName(), currentTimeMillis);
        util.checkMetric(SUFFIX_VALUE, METRIC_VALUE);
        assertEquals(1, metrics.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGaugeMetricWithNonNumberValue() {
        SortedMap<String, Gauge> gauges = new TreeMap<>();
        Gauge<String> gauge = new Gauge<String>() {
            @Override
            public String getValue() {
                return String.valueOf(METRIC_VALUE);
            }

        };
        gauges.put(GAUGE_METRIC, gauge);
        new GaugeConverter().convert(gauges, currentTimeMillis);
    }
}
package com.sap.cloud.cf.monitoring.client.model.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.sap.cloud.cf.monitoring.client.model.Metric;

public class MetricRequest implements Serializable {
    private static final long serialVersionUID = 1821203758427351658L;

    private String applicationGUID;
    private String instanceGUID;
    private int index;
    private List<Metric> metrics;

    public MetricRequest(String applicationGUID, String instanceGUID, int index, List<Metric> metrics) {
        this.applicationGUID = applicationGUID;
        this.instanceGUID = instanceGUID;
        this.index = index;
        this.metrics = new ArrayList<Metric>(metrics);
    }

    public String getApplicationGUID() {
        return applicationGUID;
    }

    public String getInstanceGUID() {
        return instanceGUID;
    }

    public int getIndex() {
        return index;
    }

    public List<Metric> getMetrics() {
        return new ArrayList<Metric>(metrics);
    }

    @Override
    public String toString() {
        return new StringBuilder("MetricRequest[").append("applicationGUID=")
            .append(applicationGUID)
            .append(", instanceGUID=")
            .append(instanceGUID)
            .append(", index=")
            .append(index)
            .append(", metrics=")
            .append(metrics)
            .append("]")
            .toString();
    }
}

package com.sap.cloud.cf.monitoring.client;

import java.util.List;

import com.sap.cloud.cf.monitoring.client.exceptions.MonitoringClientException;
import com.sap.cloud.cf.monitoring.client.model.Metric;

public interface MonitoringClient {

    /**
     * Send single metric to the Monitoring service
     *
     * @param metric
     * @throws MonitoringClientException
     */
    void send(Metric metric) throws MonitoringClientException;

    /**
     * Send list of metrics to the Monitoring service
     *
     * @param metrics
     * @throws MonitoringClientException
     */
    void send(List<Metric> metrics) throws MonitoringClientException;
}

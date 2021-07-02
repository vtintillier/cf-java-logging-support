package com.sap.cloud.cf.monitoring.client;

import static com.sap.cloud.cf.monitoring.client.utils.Utils.checkNotNull;

import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.sap.cloud.cf.monitoring.client.exceptions.MonitoringClientException;
import com.sap.cloud.cf.monitoring.client.model.Metric;
import com.sap.cloud.cf.monitoring.client.model.MetricEnvelope;

public class MonitoringClientBuilder {

    public MonitoringClient create() {
        return new MonitoringClientImpl();
    }

    static class MonitoringClientImpl implements MonitoringClient {

        static final String REQUEST_URL_TEMPLATE = "%s/v1/apps/%s/instances/%s";

        private static final Gson gson = new Gson();

        @Override
        public void send(Metric metric) throws MonitoringClientException {
            checkNotNull(metric, "metric");
            send(Arrays.asList(metric));
        }

        @Override
        public void send(List<Metric> metrics) throws MonitoringClientException {
            checkNotNull(metrics, "metrics");
            if (metrics.isEmpty()) {
                throw new IllegalArgumentException("The list of metrics cannot be empty");
            }
            MetricEnvelope envelope = new MetricEnvelope(metrics);
            String message = gson.toJson(envelope);
            System.out.println(message);
        }
    }
}

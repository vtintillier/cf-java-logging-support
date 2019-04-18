package com.sap.cloud.cf.monitoring.client;

import static com.sap.cloud.cf.monitoring.client.utils.Utils.checkNotNull;
import static java.lang.String.format;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.sap.cloud.cf.monitoring.client.configuration.ConfigurationProvider;
import com.sap.cloud.cf.monitoring.client.exceptions.MonitoringClientException;
import com.sap.cloud.cf.monitoring.client.model.Metric;
import com.sap.cloud.cf.monitoring.client.model.api.MetricRequest;

public class MonitoringClientBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(MonitoringClientBuilder.class);

    private ConfigurationProvider provider;
    private CloseableHttpClient client = null;

    public MonitoringClientBuilder setConfigurationProvider(ConfigurationProvider provider) {
        checkNotNull(provider, "configuration provider");
        this.provider = provider;
        return this;
    }

    public MonitoringClientBuilder setHttpClient(CloseableHttpClient client) {
        checkNotNull(client, "http client");
        this.client = client;
        return this;
    }

    public MonitoringClient create() {
        if (this.client == null) {
            return new MonitoringClientImpl(this.provider);
        }
        return new MonitoringClientImpl(this.provider, this.client);
    }

    static class MonitoringClientImpl implements MonitoringClient {

        static final String REQUEST_URL_TEMPLATE = "%s/v1/apps/%s/instances/%s";

        private static final Gson gson = new Gson();

        private ConfigurationProvider configurationProvider;
        private CloseableHttpClient client;
        private UsernamePasswordCredentials credentials;

        private static CloseableHttpClient createHttpClient(ConfigurationProvider provider) {
            return HttpClients.custom().useSystemProperties().build();
        }

        MonitoringClientImpl(ConfigurationProvider provider) {
            this(provider, createHttpClient(provider));
        }

        MonitoringClientImpl(ConfigurationProvider provider, CloseableHttpClient client) {
            checkNotNull(provider, "configuration provider");
            checkNotNull(client, "client");
            this.configurationProvider = provider;
            this.client = client;
        }

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
            MetricRequest metricRequest = createMetricRequest(metrics);
            String requestURL = createRequestURL();
            processRequest(requestURL, metricRequest);
        }

        private void processRequest(String requestURL, MetricRequest metricRequest) {
            HttpPost request = new HttpPost(requestURL);
            request.setEntity(createEntity(metricRequest));
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            request.addHeader(basicSchema(request));

            try (CloseableHttpResponse response = client.execute(request)) {
                checkResponseCode(response);
                EntityUtils.consumeQuietly(response.getEntity());
            } catch (IOException e) {
                throw new MonitoringClientException(
                        "Error occured while trying to send metrics to the monitoring service", e);
            }
        }

        private Header basicSchema(HttpPost request) {
            try {
                return new BasicScheme().authenticate(getCredentials(configurationProvider), request, null);
            } catch (AuthenticationException e) {
                LOG.error("Could not initialize BasicSchema");
            }
            return null;
        }

        private UsernamePasswordCredentials getCredentials(ConfigurationProvider provider) {
            if (credentials == null) {
                credentials =
                    new UsernamePasswordCredentials(provider.getClientId(), new String(provider.getClientSecret()));
            }
            return credentials;
        }

        private void checkResponseCode(CloseableHttpResponse response) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_CREATED != statusCode) {
                String message = extractErrorMessage(response);
                throw new MonitoringClientException(format(
                    "Unexpected response code from monitoring service: %d, message: %s", statusCode, message));
            }
        }

        private String extractErrorMessage(CloseableHttpResponse response) {
            try {
                return EntityUtils.toString(response.getEntity());
            } catch (Exception e) { //NOSONAR
                LOG.warn("Cannot extract the error message from the response", e);
                return "";
            }
        }

        private HttpEntity createEntity(MetricRequest metricRequest) {
            try {
                return new StringEntity(gson.toJson(metricRequest));
            } catch (UnsupportedEncodingException e) {
                throw new MonitoringClientException("Unable to create request entity", e);
            }
        }

        private String createRequestURL() {
            return String.format(REQUEST_URL_TEMPLATE, configurationProvider.getUrl(),
                configurationProvider.getApplicationGUID(), configurationProvider.getInstanceGUID());
        }

        private MetricRequest createMetricRequest(List<Metric> metrics) {
            return new MetricRequest(configurationProvider.getApplicationGUID(),
                    configurationProvider.getInstanceGUID(), configurationProvider.getInstanceIndex(), metrics);
        }
    }
}

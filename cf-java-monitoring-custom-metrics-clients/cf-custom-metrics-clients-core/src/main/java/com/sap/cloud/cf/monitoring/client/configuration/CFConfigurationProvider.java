package com.sap.cloud.cf.monitoring.client.configuration;

import static com.sap.cloud.cf.monitoring.client.utils.Utils.checkNotNull;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class CFConfigurationProvider implements ConfigurationProvider {

    public static final String VCAP_SERVICES_KEY = "VCAP_SERVICES";
    public static final String CF_INSTANCE_GUID_KEY = "CF_INSTANCE_GUID";
    public static final String CF_INSTANCE_INDEX_KEY = "CF_INSTANCE_INDEX";
    public static final String VCAP_APPLICATION_KEY = "VCAP_APPLICATION";

    public static final String METRICS_COLLECTOR_CLIENT_SECRET_KEY = "metrics_collector_client_secret";
    public static final String METRICS_COLLECTOR_CLIENT_ID_KEY = "metrics_collector_client_id";
    public static final String METRICS_COLLECTOR_URL_KEY = "metrics_collector_url";

    private static final Gson gson = new Gson();

    private final String applicationGUID;
    private final String instanceGUID;
    private final int instanceIndex;
    private final String url;
    private final String clientId;
    private final char[] clientSecret;

    /**
     * Gets the configuration from the environment variables of the process
     */
    public CFConfigurationProvider() {
        this(System.getenv());
    }

    public CFConfigurationProvider(Map<String, String> props) {
        checkNotNull(props, "configuration");
        checkNotNull(props.get(CF_INSTANCE_GUID_KEY), CF_INSTANCE_GUID_KEY);
        checkNotNull(props.get(CF_INSTANCE_INDEX_KEY), CF_INSTANCE_INDEX_KEY);
        checkNotNull(props.get(VCAP_APPLICATION_KEY), VCAP_APPLICATION_KEY);
        checkNotNull(props.get(VCAP_SERVICES_KEY), VCAP_SERVICES_KEY);

        instanceGUID = props.get(CF_INSTANCE_GUID_KEY);
        instanceIndex = Integer.parseInt(props.get(CF_INSTANCE_INDEX_KEY));
        applicationGUID = parseApplicationGUID(props);
        ApplicationLogs appLogs = parseApplicationLogs(props);
        url = getCredentialKey(appLogs, METRICS_COLLECTOR_URL_KEY);
        clientId = getCredentialKey(appLogs, METRICS_COLLECTOR_CLIENT_ID_KEY);
        clientSecret = getSecret(appLogs);
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getApplicationGUID() {
        return applicationGUID;
    }

    @Override
    public String getInstanceGUID() {
        return instanceGUID;
    }

    @Override
    public int getInstanceIndex() {
        return instanceIndex;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public char[] getClientSecret() {
        return clientSecret;
    }

    private String parseApplicationGUID(Map<String, String> props) {
        String vcapApplicationString = props.get(VCAP_APPLICATION_KEY);
        VcapApplication vcapApplication = gson.fromJson(vcapApplicationString, VcapApplication.class);
        return vcapApplication.getApplicationGUID();
    }

    private String getCredentialKey(ApplicationLogs appLogs, String key) {
        Map<String, String> credentials = appLogs.getCredentials();
        checkNotNull(credentials, "credentials");
        String value = credentials.get(key);
        checkNotNull(value, key);
        return value;
    }

    private char[] getSecret(ApplicationLogs appLogs) {
        String secret = getCredentialKey(appLogs, METRICS_COLLECTOR_CLIENT_SECRET_KEY);
        return secret.toCharArray();
    }

    private ApplicationLogs parseApplicationLogs(Map<String, String> props) {
        String vcapServicesString = props.get(VCAP_SERVICES_KEY);
        VcapServices vcapServices = gson.fromJson(vcapServicesString, VcapServices.class);
        checkNotNull(vcapServices, VCAP_SERVICES_KEY);
        List<ApplicationLogs> applicationLogsList = vcapServices.getApplicationLogs();
        if (applicationLogsList == null || applicationLogsList.isEmpty()) {
            throw new IllegalArgumentException("application-logs not found in " + VCAP_SERVICES_KEY);
        }
        return applicationLogsList.get(0);
    }

    static class VcapApplication {
        @SerializedName("application_id")
        private String applicationGUID;

        public String getApplicationGUID() {
            return applicationGUID;
        }
    }

    static class VcapServices {
        @SerializedName("application-logs")
        private List<ApplicationLogs> applicationLogs;

        public List<ApplicationLogs> getApplicationLogs() {
            return applicationLogs;
        }
    }

    static class ApplicationLogs {
        private Map<String, String> credentials;

        public Map<String, String> getCredentials() {
            return credentials;
        }
    }

}

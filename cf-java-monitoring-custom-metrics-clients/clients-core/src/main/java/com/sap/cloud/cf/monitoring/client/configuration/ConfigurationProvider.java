package com.sap.cloud.cf.monitoring.client.configuration;

public interface ConfigurationProvider {

    /**
     * @return the URL of the Metrics Collector
     */
    String getUrl();

    /**
     * @return the client id used to authenticate against the Metrics Collector
     */
    String getClientId();

    /**
     * @return the client secret used to authenticate against the Metrics Collector
     */
    char[] getClientSecret();

    /**
     * @return the application GUID
     */
    String getApplicationGUID();

    /**
     * @return the instance GUID
     */
    String getInstanceGUID();

    /**
     * @return the instance index
     */
    int getInstanceIndex();
}

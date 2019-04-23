package com.sap.cloud.cf.monitoring.client.exceptions;

public class MonitoringClientException extends RuntimeException {

    private static final long serialVersionUID = -848994007990025958L;

    public MonitoringClientException(String message) {
        super(message);
    }

    public MonitoringClientException(String message, Throwable t) {
        super(message, t);
    }
}

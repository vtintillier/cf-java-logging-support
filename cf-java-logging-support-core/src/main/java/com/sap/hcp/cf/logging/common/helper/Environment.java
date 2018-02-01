package com.sap.hcp.cf.logging.common.helper;

public class Environment {

    public static final String LOG_REMOTE_IP = "LOG_REMOTE_IP";

    public String getVariable(String name) {
        return System.getenv(name);
    }
}

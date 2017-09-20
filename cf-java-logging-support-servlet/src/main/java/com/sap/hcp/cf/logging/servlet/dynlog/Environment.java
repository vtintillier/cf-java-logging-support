package com.sap.hcp.cf.logging.servlet.dynlog;

public class Environment {

    public String getVariable(String name) {
        return System.getenv(name);
    }
}

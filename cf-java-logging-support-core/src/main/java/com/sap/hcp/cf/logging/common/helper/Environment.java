package com.sap.hcp.cf.logging.common.helper;

public class Environment {

    public String getVariable(String name) {
        return System.getenv(name);
    }
}

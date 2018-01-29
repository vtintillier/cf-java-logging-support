package com.sap.hcp.cf.logging.servlet.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.hcp.cf.logging.common.helper.Environment;

public class LogRemoteIPSettings {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogRemoteIPSettings.class);

    private final String logRemoteIPSetting;

    public LogRemoteIPSettings() {
        this(new Environment());
    }

    LogRemoteIPSettings(Environment environment) {
        String tempLogRemoteIP = environment.getVariable("LogRemoteIP");

        if (tempLogRemoteIP != null) {
            if (tempLogRemoteIP.equals("true") || tempLogRemoteIP.equals("True") || tempLogRemoteIP.equals("TRUE")) {
                tempLogRemoteIP = "true";
                LOGGER.info("Logging remote IPs has been ACTIVATED via environment variable");
            } else if (tempLogRemoteIP.equals("false") || tempLogRemoteIP.equals("False") || tempLogRemoteIP.equals(
                                                                                                                    "FALSE")) {
                tempLogRemoteIP = "false";
                LOGGER.info("Logging remote IPs has been DEACTIVATED via environment variable");
            } else {
                LOGGER.error("Logging remote IPs is DEACTIVATED by default. Environment variable \"LogRemoteIP = {}\" could not " +
                             "be read. It should be set to true to activate Logging of remote IPs or to false to deactivate it",
                             tempLogRemoteIP);
                tempLogRemoteIP = "false";
            }
        } else {
            tempLogRemoteIP = "false";
            LOGGER.info("Logging remote IPs is DEACTIVATED by default.");
        }
        logRemoteIPSetting = tempLogRemoteIP;
    }

    public String getLogRemoteIPSetting() {
        return logRemoteIPSetting;
    }
}

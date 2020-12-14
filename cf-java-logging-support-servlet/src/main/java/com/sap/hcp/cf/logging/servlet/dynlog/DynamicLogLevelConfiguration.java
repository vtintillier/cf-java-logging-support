package com.sap.hcp.cf.logging.servlet.dynlog;

import java.security.interfaces.RSAPublicKey;

import javax.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface DynamicLogLevelConfiguration {

    RSAPublicKey getRsaPublicKey();

    default String getDynLogHeaderKey() {
        return "SAP-LOG-LEVEL";
    };

    default String getDynLogHeaderValue(HttpServletRequest httpRequest) {
        return httpRequest.getHeader(getDynLogHeaderKey());
    }

}

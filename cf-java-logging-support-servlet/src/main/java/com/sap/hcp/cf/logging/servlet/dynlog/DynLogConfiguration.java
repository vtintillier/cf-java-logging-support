package com.sap.hcp.cf.logging.servlet.dynlog;

import java.security.interfaces.RSAPublicKey;

import javax.servlet.http.HttpServletRequest;

public interface DynLogConfiguration {

    String getDynLogHeaderKey();

    RSAPublicKey getRsaPublicKey();

    default String getDynLogHeaderValue(HttpServletRequest httpRequest) {
        return httpRequest.getHeader(getDynLogHeaderKey());
    }

}

package com.sap.hcp.cf.logging.servlet.dynlog;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DynLogEnvironment {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynLogEnvironment.class);
    private final RSAPublicKey rsaPublicKey;
    private final String dynLogHeaderKey;

    public DynLogEnvironment() {
        this(new Environment());
    }

    DynLogEnvironment(Environment environment) {
        String header = environment.getVariable("DYN_LOG_HEADER");
        if (header != null) {
            dynLogHeaderKey = header;
            LOGGER.info("The header key used to retrieve the dynamic log level token has been set to {}", header);
        } else {
            dynLogHeaderKey = "SAP-LOG-LEVEL";
            LOGGER.info("The header key used to retrieve the dynamic log level token has been set to the default value: {}",
                        dynLogHeaderKey);
        }
        PublicKeyReader publicKeyReader = new PublicKeyReader();

        RSAPublicKey tempKey = null;
        try {
            tempKey = publicKeyReader.readPublicKey(environment);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Could not read RSAPublicKey from environment", e);
        } catch (InvalidKeySpecException e) {
            LOGGER.error("Could not read RSAPublicKey from environment", e);
        } catch (IOException e) {
            LOGGER.error("Could not read RSAPublicKey from environment", e);
        } finally {
            rsaPublicKey = tempKey;
        }
    }

    public RSAPublicKey getRsaPublicKey() {
        return rsaPublicKey;
    }

    public String getDynLogHeaderKey() {
        return dynLogHeaderKey;
    }
}

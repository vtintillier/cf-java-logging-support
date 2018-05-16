package com.sap.hcp.cf.logging.servlet.dynlog;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.sap.hcp.cf.logging.common.helper.DynamicLogLevelHelper;

/**
 * This class provides a mechanism that reads a token from an
 * HTTP-request-header. If this token is provided and does contain a correct
 * signature, valid timestamps and a log-level-value, the log-level for the
 * thread triggered by this request will be changed to the provided value.
 */
public class DynamicLogLevelProcessor {

    private final static Logger LOGGER = LoggerFactory.getLogger(DynamicLogLevelProcessor.class);
    private static final List<String> ALLOWED_DYNAMIC_LOGLEVELS = Arrays.asList("TRACE", "DEBUG", "INFO", "WARN",
                                                                                "ERROR");
    private final TokenDecoder tokenDecoder;
    private final DynLogEnvironment dynLogEnvironment;

    public DynamicLogLevelProcessor(DynLogEnvironment dynLogEnvironment) {
        this.dynLogEnvironment = dynLogEnvironment;
        this.tokenDecoder = new TokenDecoder(dynLogEnvironment.getRsaPublicKey());
    }

    public void copyDynamicLogLevelToMDC(HttpServletRequest httpRequest) {
        String logLevelToken = httpRequest.getHeader(dynLogEnvironment.getDynLogHeaderKey());
        if (logLevelToken == null) {
            return;
        } else {
            try {
                DecodedJWT jwt = tokenDecoder.validateAndDecodeToken(logLevelToken);
                String dynamicLogLevel = jwt.getClaim("level").asString();
                if (ALLOWED_DYNAMIC_LOGLEVELS.contains(dynamicLogLevel)) {
                    MDC.put(DynamicLogLevelHelper.MDC_DYNAMIC_LOG_LEVEL_KEY, dynamicLogLevel);
                } else {
                    throw new DynamicLogLevelException("Dynamic Log-Level [" + dynamicLogLevel +
                                                       "] provided in header is not valid. Allowed Values are " +
                                                       ALLOWED_DYNAMIC_LOGLEVELS.toString());
                }
            } catch (DynamicLogLevelException e) {
                LOGGER.warn("DynamicLogLevelProcessor could not write dynamic log level to MDC", e);
            }
        }
    }

    public void removeDynamicLogLevelFromMDC() {
        MDC.remove(DynamicLogLevelHelper.MDC_DYNAMIC_LOG_LEVEL_KEY);
    }
}

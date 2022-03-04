package com.sap.hcp.cf.logging.common.serialization;

public class JsonSerializationException extends RuntimeException {

    private static final long serialVersionUID = 6529980121185308722L;

    public JsonSerializationException(String message, Throwable cause) {
        super(message, cause);
    }

}

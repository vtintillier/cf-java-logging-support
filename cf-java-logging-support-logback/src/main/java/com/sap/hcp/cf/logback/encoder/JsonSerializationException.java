package com.sap.hcp.cf.logback.encoder;

public class JsonSerializationException extends RuntimeException {

    private static final long serialVersionUID = 8365183525285128131L;

    public JsonSerializationException(String message, Throwable cause) {
        super(message, cause);
    }

}

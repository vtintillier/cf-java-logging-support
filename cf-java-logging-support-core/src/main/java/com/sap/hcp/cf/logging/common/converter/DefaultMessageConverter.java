package com.sap.hcp.cf.logging.common.converter;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jr.ob.JSON;

public class DefaultMessageConverter {

    private boolean flatten = false;
    private boolean escape = false;
    private static final String LBRACE = "{";
    private static final String RBRACE = "}";
    private static final String LBRACKET = "[";
    private static final String RBRACKET = "]";

    public boolean isFlatten() {
        return flatten;
    }

    public void setFlatten(boolean flatten) {
        this.flatten = flatten;
    }

    public boolean isEscape() {
        return escape;
    }

    public void setEscape(boolean escape) {
        this.escape = escape;
    }

    public void convert(String message, StringBuilder appendTo) {
        if (message != null) {
            String result;
            if (flatten) {
                result = flattenMsg(message);
            } else {
                result = message;
            }
            if (escape) {
                try {
                    appendTo.append(JSON.std.asString(result));
                } catch (Exception ex) {
                    /* -- avoids substitute logger warnings on startup -- */
                    LoggerFactory.getLogger(DefaultMessageConverter.class).error("Conversion failed ", ex);
                    appendTo.append(result);
                }
            } else {
                appendTo.append(result);
            }
        } else {
            appendTo.append("null");
        }

    }

    private String flattenMsg(String msg) {
        String trimmedMsg = msg.trim();

        if (trimmedMsg.indexOf(LBRACE) == 0 && trimmedMsg.lastIndexOf(RBRACE) == trimmedMsg.length() - 1) {
            return trimmedMsg.substring(1, trimmedMsg.length() - 1);
        }
        if (trimmedMsg.indexOf(LBRACKET) == 0 && trimmedMsg.lastIndexOf(RBRACKET) == trimmedMsg.length() - 1) {
            return trimmedMsg.substring(1, trimmedMsg.length() - 1);
        }
        return msg;
    }

}

package com.sap.hcp.cf.logging.servlet.filter;

import java.util.Arrays;
import java.util.List;

import com.sap.hcp.cf.logging.common.request.HttpHeader;

public class HttpTestHeader implements HttpHeader {

    private String name;
    private String field;
    private String fieldValue;
    private boolean propagated;
    private List<HttpHeader> aliases;

    public HttpTestHeader(String name, String field, String fieldValue, boolean propagated, HttpHeader... aliases) {
        this.name = name;
        this.field = field;
        this.fieldValue = fieldValue;
        this.propagated = propagated;
        this.aliases = Arrays.asList(aliases);
    }

    @Override
    public boolean isPropagated() {
        return propagated;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public List<HttpHeader> getAliases() {
        return aliases;
    }

    @Override
    public String getFieldValue() {
        return fieldValue;
    }

}

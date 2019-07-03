package com.sap.hcp.cf.logging.common.converter;

import java.io.IOException;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;

public abstract class AbstractConverterTest {
    protected static final String EMPTY = "";
    protected static final String STRANGE_SEQ = "}{:\",\"";
    protected static final String TEST_MSG_NO_ARGS = "This is a test ";

    protected String formatMsg(DefaultMessageConverter mc, String msg) {
        StringBuilder sb = new StringBuilder();
        mc.convert(msg, sb);
        return sb.toString();
    }

    protected String formatStacktrace(DefaultStacktraceConverter dstc, Throwable t) {
        StringBuilder sb = new StringBuilder();
        dstc.convert(t, sb);
        return sb.toString();
    }

    protected Object arrayElem(String serialized, int i) throws JSONObjectException, IOException {
        return arrayFrom(serialized)[i];
    }

    protected Object[] arrayFrom(String serialized) throws JSONObjectException, IOException {
        return JSON.std.arrayFrom(serialized);
    }
}

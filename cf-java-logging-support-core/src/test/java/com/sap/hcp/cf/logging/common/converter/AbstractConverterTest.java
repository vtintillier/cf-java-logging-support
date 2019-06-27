package com.sap.hcp.cf.logging.common.converter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.MDC;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;

public abstract class AbstractConverterTest {
    protected static final String EMPTY = "";
    protected static final String SOME_KEY = "some_key";
    protected static final String SOME_VALUE = "some value";
    protected static final String STRANGE_SEQ = "}{:\",\"";
    protected static final String SOME_OTHER_KEY = "some_other_key";
    protected static final String SOME_OTHER_VALUE = "some other value";
    protected static final String TEST_MSG_NO_ARGS = "This is a test ";

    protected String formatMsg(DefaultMessageConverter mc, String msg) {
        StringBuilder sb = new StringBuilder();
        mc.convert(msg, sb);
        return sb.toString();
    }

    protected String formatProps(DefaultPropertiesConverter pc) {
        StringBuilder sb = new StringBuilder();
        pc.convert(MDC.getCopyOfContextMap(), sb);
        return sb.toString();
    }

    protected String formatStacktrace(DefaultStacktraceConverter dstc, Throwable t) {
        StringBuilder sb = new StringBuilder();
        dstc.convert(t, sb);
        return sb.toString();
    }

    protected Map<String, Object> mdcMap() {
        return mdcMap(null);
    }

    protected Map<String, Object> mdcMap(String[] exclusions) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<String> exclusionList;
        if (exclusions == null) {
            exclusionList = Arrays.asList(new String[0]);
        } else {
            exclusionList = Arrays.asList(exclusions);
        }
        for (Entry<String, String> t: MDC.getCopyOfContextMap().entrySet()) {
            if (!exclusionList.contains(t.getKey())) {
                result.put(t.getKey(), t.getValue());
            }
        }
        return result;
    }

    protected Object arrayElem(String serialized, int i) throws JSONObjectException, IOException {
        return arrayFrom(serialized)[i];
    }

    protected Object[] arrayFrom(String serialized) throws JSONObjectException, IOException {
        return JSON.std.arrayFrom(serialized);
    }

    protected Map<String, Object> mapFrom(String serialized) throws JSONObjectException, IOException {
        return mapFrom(serialized, true);
    }

    protected Map<String, Object> mapFrom(String serialized, boolean wrap) throws JSONObjectException, IOException {
        if (wrap) {
            return JSON.std.mapFrom("{" + serialized + "}");
        } else {
            return JSON.std.mapFrom(serialized);
        }
    }
}

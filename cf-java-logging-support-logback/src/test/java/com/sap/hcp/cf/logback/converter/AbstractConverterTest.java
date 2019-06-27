package com.sap.hcp.cf.logback.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

public abstract class AbstractConverterTest {
    protected static final String PREFIX = "prefix";
    protected static final String EMPTY = "";
    protected static final String SOME_KEY = "some_key";
    protected static final String SOME_VALUE = "some value";
    protected static final String STRANGE_SEQ = "}{:\",\"";
    protected static final String SOME_OTHER_KEY = "some_other_key";
    protected static final String SOME_OTHER_VALUE = "some other value";
    protected static final String TEST_MSG_NO_ARGS = "This is a test ";
    protected static final Object[] NO_ARGS = new Object[0];

    protected LoggingEvent makeEvent(String msg, Object[] args) {
        return makeEvent(msg, null, args);
    }

    protected LoggingEvent makeEvent(String msg, Throwable t, Object[] args) {
        Logger logger = LoggerFactory.getLogger(this.getClass().getName());
        return new LoggingEvent(this.getClass().getName(), (ch.qos.logback.classic.Logger) logger, Level.INFO, msg, t,
                                args);
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
        }
        return JSON.std.mapFrom(serialized);
    }

    protected Map<String, Object> mdcMap() {
        return mdcMap(null);
    }

    protected Map<String, Object> mdcMap(List<String> exclusions) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (exclusions == null) {
            exclusions = new ArrayList<String>();
        }
        for (Entry<String, String> t: MDC.getCopyOfContextMap().entrySet()) {
            if (!exclusions.contains(t.getKey())) {
                result.put(t.getKey(), t.getValue());
            }
        }
        return result;
    }
}

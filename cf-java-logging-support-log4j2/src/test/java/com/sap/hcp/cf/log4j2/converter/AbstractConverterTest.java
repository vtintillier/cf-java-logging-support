package com.sap.hcp.cf.log4j2.converter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent.Builder;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;

public abstract class AbstractConverterTest {
    protected static final String SOME_KEY = "some_key";
    protected static final String SOME_VALUE = "some value";
    protected static final String STRANGE_SEQ = "}{:\",\"";
    protected static final String SOME_OTHER_KEY = "some_other_key";
    protected static final String SOME_OTHER_VALUE = "some other value";
    protected static final String TEST_MSG_NO_ARGS = "This is a test ";
    protected static final Object[] NO_ARGS = new Object[0];

    @Before
    public void start() {
        Logger logger = LoggerFactory.getLogger(AbstractConverterTest.class);
        logger.debug("starting");
    }

    protected String format(LogEventPatternConverter converter, LogEvent event) {
        StringBuilder sb = new StringBuilder();
        converter.format(event, sb);
        return sb.toString();
    }

    protected LogEvent makeEvent(String msg, Object[] args) {
        return makeEvent(msg, null, args);
    }

    protected LogEvent makeEvent(String msg, Throwable t, Object[] args) {
        Message message;
        if (args == null || args.length == 0) {
            message = new SimpleMessage(msg);
        } else {
            message = new ParameterizedMessage(msg, args);
        }
        LogEvent event = new Builder().setLevel(Level.INFO).setMessage(message).setThrown(t).build();
        return event;
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
}

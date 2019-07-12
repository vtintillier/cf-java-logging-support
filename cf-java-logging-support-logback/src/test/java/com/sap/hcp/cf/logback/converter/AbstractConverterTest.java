package com.sap.hcp.cf.logback.converter;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

public abstract class AbstractConverterTest {
    protected static final String STRANGE_SEQ = "}{:\",\"";
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
}

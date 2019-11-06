package com.sap.hcp.cf.logging.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.jr.ob.JSON;

public abstract class AbstractTest {

    public static final String TEST_MESSAGE = "this is a test message";
    public static final String SOME_KEY = "some_key";
    public static final String SOME_VALUE = "some value";
    public static final Double SOME_DOUBLE_VALUE = 12.34;
    public static final String SOME_OTHER_KEY = "some_other_key";
    public static final String SOME_OTHER_VALUE = "some other value";

    protected final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    protected final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

    @Before
    public void setupStreams() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @After
    public void teardownStreams() {
        System.setOut(null);
        System.setErr(null);
    }

    protected String getMessage() {
        return getField("msg");
    }

    protected String getField(String fieldName) {
        try {
            return JSON.std.mapFrom(lastLine()).get(fieldName).toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    protected List<String> getList(String fieldName) {
        try {
            return (List<String>) JSON.std.mapFrom(lastLine()).get(fieldName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private String lastLine() {
        String[] lines = outContent.toString().split("\n");
        return lines[lines.length - 1];
    }

    protected Object getCustomField(String fieldName) {
        Map<String, Object> cfMap = getMap("custom_fields");
        Object fObj = cfMap.get(fieldName);
        if (fObj != null) {
             return fObj;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> getMap(String fieldName) {
        try {
            return (Map<String, Object>) JSON.std.mapFrom(outContent.toString()).get(fieldName);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected boolean hasField(String fieldName) {
        return getField(fieldName) != null;
    }
}

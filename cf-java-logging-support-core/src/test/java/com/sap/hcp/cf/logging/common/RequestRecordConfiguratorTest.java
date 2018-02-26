package com.sap.hcp.cf.logging.common;

import static com.sap.hcp.cf.logging.common.RequestRecordConfigurator.to;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;

public class RequestRecordConfiguratorTest {

    @Test
    public void testAddingSingleActivatedOptionalTagToRequestRecord() throws JSONObjectException, IOException {
        RequestRecord requestRecord = new RequestRecord("TEST");
        boolean canBeLogged = true;
        String key = "TestKey";
        String tag = "TestTag";

        to(requestRecord).addOptionalTag(canBeLogged, key, tag);
        requestRecord.close();

        assertEquals(tag, getFieldFromRequestRecord(requestRecord, key));
    }

    @Test
    public void testAddingSingleForbiddenOptionalTagToRequestRecord() throws JSONObjectException, IOException {
        RequestRecord requestRecord = new RequestRecord("TEST");
        boolean canBeLogged = false;
        String key = "TestKey";
        String tag = "TestTag";

        to(requestRecord).addOptionalTag(canBeLogged, key, tag);
        requestRecord.close();

        assertEquals(Defaults.REDACTED, getFieldFromRequestRecord(requestRecord, key));
    }

    @Test
    public void testAddingSingleForbiddenOptionalNullTagToRequestRecord() throws JSONObjectException, IOException {
        RequestRecord requestRecord = new RequestRecord("TEST");
        boolean canBeLogged = false;
        String key = "TestKey";
        String tag = Defaults.UNKNOWN;

        to(requestRecord).addOptionalTag(canBeLogged, key, tag);
        requestRecord.close();

        assertEquals(Defaults.UNKNOWN, getFieldFromRequestRecord(requestRecord, key));
    }

    @Test
    public void testAddingSingleActivatedOptionalNullTagToRequestRecord() throws JSONObjectException, IOException {
        RequestRecord requestRecord = new RequestRecord("TEST");
        boolean canBeLogged = true;
        String key = "TestKey";
        String tag = Defaults.UNKNOWN;

        to(requestRecord).addOptionalTag(canBeLogged, key, tag);
        requestRecord.close();

        assertEquals(Defaults.UNKNOWN, getFieldFromRequestRecord(requestRecord, key));
    }

    private String getFieldFromRequestRecord(RequestRecord requestRecord, String key) throws JSONObjectException,
                                                                                      IOException {
        return JSON.std.mapFrom(requestRecord.toString()).get(key).toString();
    }
}

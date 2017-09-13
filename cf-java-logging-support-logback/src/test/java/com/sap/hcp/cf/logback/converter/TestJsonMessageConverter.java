package com.sap.hcp.cf.logback.converter;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sap.hcp.cf.logging.common.RequestRecord;

public class TestJsonMessageConverter extends AbstractConverterTest {

    private static final String ARRAY_MSG = "[1, 2, 3, 4]";
    private static final String OBJ_MSG = "{\"foo\":\"bar\", \"baz\":1}";
    private static final String LOG_PROVIDER = "test";

    @Test
    public void testSimple() {
        JsonMessageConverter jmc = new JsonMessageConverter();
        jmc.start();
        assertThat(jmc.convert(makeEvent(TEST_MSG_NO_ARGS, NO_ARGS)), is(TEST_MSG_NO_ARGS));
    }

    @Test
    public void testSimpleQuoted() {
        JsonMessageConverter jmc = new JsonMessageConverter();
        String quotedMsg = TEST_MSG_NO_ARGS + " with a \"quote\"";
        jmc.start();
        assertThat(jmc.convert(makeEvent(quotedMsg, NO_ARGS)), is(quotedMsg));
    }

    @Test
    public void testArrayMsgNotFlattened() {
        JsonMessageConverter jmc = new JsonMessageConverter();
        jmc.start();
        assertThat(jmc.convert(makeEvent(ARRAY_MSG, NO_ARGS)), is(ARRAY_MSG));
    }

    @Test
    public void testArrayMsgFlattened() {
        JsonMessageConverter jmc = new JsonMessageConverter();
        jmc.setOptionList(asList("flatten"));
        jmc.start();
        assertThat(jmc.convert(makeEvent(ARRAY_MSG, NO_ARGS)), is(ARRAY_MSG.substring(1, ARRAY_MSG.length() - 1)));
    }

    @Test
    public void testObjMsgNotFlattened() {
        JsonMessageConverter jmc = new JsonMessageConverter();
        jmc.start();
        assertThat(jmc.convert(makeEvent(OBJ_MSG, NO_ARGS)), is(OBJ_MSG));
    }

    @Test
    public void testObjMsgFlattened() {
        JsonMessageConverter jmc = new JsonMessageConverter();
        jmc.setOptionList(asList("flatten"));
        jmc.start();
        assertThat(jmc.convert(makeEvent(OBJ_MSG, NO_ARGS)), is(OBJ_MSG.substring(1, OBJ_MSG.length() - 1)));
    }

    @Test
    public void testLogRecordMsgNotFlattened() {
        JsonMessageConverter jmc = new JsonMessageConverter();
        jmc.start();
        RequestRecord lrec = new RequestRecord(LOG_PROVIDER);
        String lmsg = lrec.toString();
        assertThat(jmc.convert(makeEvent(lmsg, NO_ARGS)), is(lmsg));
        lrec.close();
    }

    @Test
    public void testLogRecordMsgFlattened() {
        JsonMessageConverter jmc = new JsonMessageConverter();
        jmc.setOptionList(asList("flatten"));
        jmc.start();
        RequestRecord lrec = new RequestRecord(LOG_PROVIDER);
        String lmsg = lrec.toString();
        assertThat(jmc.convert(makeEvent(lmsg, NO_ARGS)), is(lmsg.substring(1, lmsg.length() - 1)));
        lrec.close();
    }

    @Test
    public void testEscapedMessage() {
        JsonMessageConverter jmc = new JsonMessageConverter();
        String strangeMsg = TEST_MSG_NO_ARGS + STRANGE_SEQ;
        jmc.start();
        assertThat(jmc.convert(makeEvent(strangeMsg, NO_ARGS)), is(strangeMsg));

    }
}

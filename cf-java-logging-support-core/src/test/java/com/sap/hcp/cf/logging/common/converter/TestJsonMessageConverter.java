package com.sap.hcp.cf.logging.common.converter;

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
        DefaultMessageConverter jmc = new DefaultMessageConverter();
        assertThat(formatMsg(jmc, TEST_MSG_NO_ARGS), is(TEST_MSG_NO_ARGS));
    }

    @Test
    public void testSimpleQuoted() {
        DefaultMessageConverter jmc = new DefaultMessageConverter();
        String quotedMsg = TEST_MSG_NO_ARGS + " with a \"quote\"";
        assertThat(formatMsg(jmc, quotedMsg), is(quotedMsg));
    }

    @Test
    public void testArrayMsgNotFlattened() {
        DefaultMessageConverter jmc = new DefaultMessageConverter();
        assertThat(formatMsg(jmc, ARRAY_MSG), is(ARRAY_MSG));
    }

    @Test
    public void testArrayMsgFlattened() {
        DefaultMessageConverter jmc = new DefaultMessageConverter();
        jmc.setFlatten(true);
        assertThat(formatMsg(jmc, ARRAY_MSG), is(ARRAY_MSG.substring(1, ARRAY_MSG.length() - 1)));
    }

    @Test
    public void testObjMsgNotFlattened() {
        DefaultMessageConverter jmc = new DefaultMessageConverter();
        assertThat(formatMsg(jmc, OBJ_MSG), is(OBJ_MSG));
    }

    @Test
    public void testObjMsgFlattened() {
        DefaultMessageConverter jmc = new DefaultMessageConverter();
        jmc.setFlatten(true);
        assertThat(formatMsg(jmc, OBJ_MSG), is(OBJ_MSG.substring(1, OBJ_MSG.length() - 1)));
    }

    @Test
    public void testLogRecordMsgNotFlattened() {
        DefaultMessageConverter jmc = new DefaultMessageConverter();
        RequestRecord lrec = new RequestRecord(LOG_PROVIDER);
        String lmsg = lrec.toString();
        assertThat(formatMsg(jmc, lmsg), is(lmsg));
        lrec.close();
    }

    @Test
    public void testLogRecordMsgFlattened() {
        DefaultMessageConverter jmc = new DefaultMessageConverter();
        jmc.setFlatten(true);
        RequestRecord lrec = new RequestRecord(LOG_PROVIDER);
        String lmsg = lrec.toString();
        assertThat(formatMsg(jmc, lmsg), is(lmsg.substring(1, lmsg.length() - 1)));
        lrec.close();
    }

    @Test
    public void testEscapedMessage() {
        DefaultMessageConverter jmc = new DefaultMessageConverter();
        String strangeMsg = TEST_MSG_NO_ARGS + STRANGE_SEQ;
        assertThat(formatMsg(jmc, strangeMsg), is(strangeMsg));
    }

    @Test
    public void testObjNestedBraces() {
        String nestedBracesMsg = "\"request\": \"/Foo(${bar})\"";
        String logMsg = " {" + nestedBracesMsg + "}  ";
        DefaultMessageConverter jmc = new DefaultMessageConverter();
        jmc.setFlatten(true);
        assertThat(formatMsg(jmc, logMsg), is(nestedBracesMsg));
    }

    @Test
    public void testObjIncompleteNestedBraces() {
        String nestedBracesMsg = "\"request\": \"/Foo(${bar)\"";
        String logMsg = " {" + nestedBracesMsg + "}  ";
        DefaultMessageConverter jmc = new DefaultMessageConverter();
        jmc.setFlatten(true);
        assertThat(formatMsg(jmc, logMsg), is(nestedBracesMsg));
    }

    @Test
    public void testInvalidObjNestedBraces() {
        String nestedBracesMsg = "\"request\": \"/Foo(${bar)\"";
        String logMsg = " {" + nestedBracesMsg;
        DefaultMessageConverter jmc = new DefaultMessageConverter();
        jmc.setFlatten(true);
        assertThat(formatMsg(jmc, logMsg), is(logMsg));
    }

    @Test
    public void testObjNestedBrackets() {
        String nestedBracketsMsg = "\"request\", \"/Foo($[bar])\"";
        String logMsg = " [" + nestedBracketsMsg + "]  ";
        DefaultMessageConverter jmc = new DefaultMessageConverter();
        jmc.setFlatten(true);
        assertThat(formatMsg(jmc, logMsg), is(nestedBracketsMsg));
    }

}

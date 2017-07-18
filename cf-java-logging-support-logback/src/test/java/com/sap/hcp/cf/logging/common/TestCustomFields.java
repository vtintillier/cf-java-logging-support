package com.sap.hcp.cf.logging.common;

import static com.sap.hcp.cf.logging.common.customfields.CustomField.customField;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.StringContainsInOrder.stringContainsInOrder;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCustomFields extends AbstractTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCustomFields.class);

    @Test
    public void testLogMessage() {
        LOGGER.info(TEST_MESSAGE);
        assertThat(getMessage(), is(TEST_MESSAGE));
    }

    @Test
    public void testLogMessageWithCustomField() {
        LOGGER.info(TEST_MESSAGE, customField(SOME_KEY, SOME_VALUE));

        assertThat(getMessage(), is(TEST_MESSAGE));
        assertThat(getCustomField(SOME_KEY), is(SOME_VALUE));
    }

    @Test
    public void testCustomFieldAsPartOfMessage() {
        String messageWithPattern = TEST_MESSAGE + " {}";
        String messageWithKeyValue = TEST_MESSAGE + " " + SOME_KEY + "=" + SOME_VALUE;

        LOGGER.info(messageWithPattern, customField(SOME_KEY, SOME_VALUE));

        assertThat(getMessage(), is(messageWithKeyValue));
        assertThat(getCustomField(SOME_KEY), is(SOME_VALUE));
    }

    @Test
    public void testEscape() {
        String messageWithPattern = TEST_MESSAGE + " {}";
        String strangeCharacters = "}{:\",\"";
        String messageWithKeyValue = TEST_MESSAGE + " " + SOME_KEY + "=" + strangeCharacters;

        LOGGER.info(messageWithPattern, customField(SOME_KEY, strangeCharacters));

        assertThat(getMessage(), is(messageWithKeyValue));
        assertThat(getCustomField(SOME_KEY), is(strangeCharacters));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullKey() {
        customField(null, SOME_VALUE);
    }

    @Test
    public void testNullValue() {
        LOGGER.info(TEST_MESSAGE, customField(SOME_KEY, null));

        assertThat(getMessage(), is(TEST_MESSAGE));
        assertThat(getCustomField(SOME_KEY), is("null"));
    }

    @Test
    public void testLogMessageWithTwoCustomFields() {
        LOGGER.info(TEST_MESSAGE, customField(SOME_KEY, SOME_VALUE), customField(SOME_OTHER_KEY, SOME_OTHER_VALUE));

        assertThat(getMessage(), is(TEST_MESSAGE));

        assertThat(getCustomField(SOME_KEY), is(SOME_VALUE));
        assertThat(getCustomField(SOME_OTHER_KEY), is(SOME_OTHER_VALUE));
    }

    @Test
    public void testOrderOfLogMessageWithTwoCustomFields() {
        LOGGER.info(TEST_MESSAGE, customField(SOME_KEY, SOME_VALUE), customField(SOME_OTHER_KEY, SOME_OTHER_VALUE));

        String jsonString = getCustomFields();
        assertThat(jsonString, stringContainsInOrder(asList(SOME_KEY, SOME_OTHER_KEY)));
        assertThat(jsonString, stringContainsInOrder(asList(SOME_VALUE, SOME_OTHER_VALUE)));
    }

    private String getCustomFields() {
        return getField("custom_fields");
    }
}

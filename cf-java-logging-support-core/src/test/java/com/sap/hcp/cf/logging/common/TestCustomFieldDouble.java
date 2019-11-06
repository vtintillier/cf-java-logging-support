package com.sap.hcp.cf.logging.common;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.sap.hcp.cf.logging.common.customfields.CustomField;


public class TestCustomFieldDouble {
	
	private static String CUSTOM_FIELD_KEY = "my_custom_field";


    @Test
    public void test() {
        double value = 123.457;

        assertThat(CustomField.customField(CUSTOM_FIELD_KEY, value).getValue(), is(123.457));
    }

}

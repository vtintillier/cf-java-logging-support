package com.sap.hcp.cf.logging.common;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class TestDoubleValue {

    @Test
    public void test() {
        double value = 123.456789;

        assertThat(new DoubleValue(value).toString(), is("123.457"));
    }

}

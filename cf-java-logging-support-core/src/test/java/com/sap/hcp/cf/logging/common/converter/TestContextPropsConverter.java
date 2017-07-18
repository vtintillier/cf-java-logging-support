package com.sap.hcp.cf.logging.common.converter;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.slf4j.MDC;

import com.fasterxml.jackson.jr.ob.JSONObjectException;

public class TestContextPropsConverter extends AbstractConverterTest {

    @Test
    public void testEmpty() throws JSONObjectException, IOException {
        DefaultPropertiesConverter cpc = new DefaultPropertiesConverter();
        MDC.clear();
        assertThat(mapFrom(formatProps(cpc)), is(mdcMap()));
    }

    @Test
    public void testSingleArg() throws JSONObjectException, IOException {
        DefaultPropertiesConverter cpc = new DefaultPropertiesConverter();
        MDC.clear();
        MDC.put(SOME_KEY, SOME_VALUE);
        assertThat(mapFrom(formatProps(cpc)), is(mdcMap()));
    }

    @Test
    public void testTwoArgs() throws JSONObjectException, IOException {
        DefaultPropertiesConverter cpc = new DefaultPropertiesConverter();
        MDC.clear();
        MDC.put(SOME_KEY, SOME_VALUE);
        MDC.put(SOME_OTHER_KEY, SOME_OTHER_VALUE);
        assertThat(mapFrom(formatProps(cpc)), is(mdcMap()));
    }

    @Test
    public void testStrangeArgs() throws JSONObjectException, IOException {
        DefaultPropertiesConverter cpc = new DefaultPropertiesConverter();
        MDC.clear();
        MDC.put(SOME_KEY, SOME_VALUE);
        MDC.put(STRANGE_SEQ, STRANGE_SEQ);
        assertThat(mapFrom(formatProps(cpc)), is(mdcMap()));
    }

    @Test
    public void testExclusion() throws JSONObjectException, IOException {
        DefaultPropertiesConverter cpc = new DefaultPropertiesConverter();
        String[] exclusions = new String[] { SOME_KEY };
        cpc.setExclusions(Arrays.asList(exclusions));
        MDC.clear();
        MDC.put(SOME_KEY, SOME_VALUE);
        MDC.put(SOME_OTHER_KEY, SOME_OTHER_VALUE);
        assertThat(mapFrom(formatProps(cpc)), is(mdcMap(exclusions)));
    }

    @Test
    public void testExclusionStrangeSeq() throws JSONObjectException, IOException {
        DefaultPropertiesConverter cpc = new DefaultPropertiesConverter();
        String[] exclusions = new String[] { STRANGE_SEQ };
        cpc.setExclusions(Arrays.asList(exclusions));
        MDC.clear();
        MDC.put(SOME_KEY, SOME_VALUE);
        MDC.put(STRANGE_SEQ, STRANGE_SEQ);
        assertThat(mapFrom(formatProps(cpc)), is(mdcMap(exclusions)));
    }
}

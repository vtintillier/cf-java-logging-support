package com.sap.hcp.cf.log4j2.converter;

import java.util.Arrays;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

import com.sap.hcp.cf.logging.common.converter.DefaultPropertiesConverter;

/**
 * A simple {@link LogEventPatternConverter} implementation that converts
 * key/value pairs from the {@link org.slf4j.MDC}. These key/value pairs are
 * embedded in the JSON message, i.e. the appear as fields at the top-level JSON
 * object.
 * <p>
 * There are two exceptions to this:
 * <ol>
 * <li>The predefined keys from
 * {@link com.sap.hcp.cf.logging.common.Fields}</li>
 * <li>The list of key names that have been passed in as options.</li>
 * </ol>
 *
 */
@Plugin(name = "ContextPropsConverter", category = "Converter")
@ConverterKeys({ "ctxp" })
public class ContextPropsConverter extends LogEventPatternConverter {

    public static final String WORD = "ctxp";
    private final DefaultPropertiesConverter converter = new DefaultPropertiesConverter();

    public ContextPropsConverter(String[] options) {
        super(WORD, WORD);
        if (options != null) {
            converter.setExclusions(Arrays.asList(options));
        }
    }

    public static ContextPropsConverter newInstance(final String[] options) {
        return new ContextPropsConverter(options);
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        converter.convert(toAppendTo, event.getContextMap());
    }

}

package com.sap.hcp.cf.log4j2.converter;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;

import com.sap.hcp.cf.logging.common.converter.DefaultCustomFieldsConverter;
import com.sap.hcp.cf.logging.common.customfields.CustomField;

/**
 * This is a simple {@link LogEventPatternConverter} implementation that
 * converts key/value pairs stored in {@link CustomField} instances which have
 * been passed as arguments.
 * <p>
 * We allow to types of addition to a log message, either <i>embedded</i>, i.e.
 * the key/value pairs appear as a list of JSON fields in the message, or as a
 * nested object where the field name has been specified as an option to this
 * converter.
 */
@Plugin(name = "ArgsConverter", category = "Converter")
@ConverterKeys({ "args" })
public class ArgsConverter extends LogEventPatternConverter {

    public static final String WORD = "args";
    private final DefaultCustomFieldsConverter converter = new DefaultCustomFieldsConverter();

    public ArgsConverter(String[] options) {
        super(WORD, WORD);
        if (options != null) {
            if (options.length == 1) {
                converter.setFieldName(options[0]);
            }
        }
    }

    public static ArgsConverter newInstance(final String[] options) {
        return new ArgsConverter(options);
    }

    @Override
    public void format(LogEvent event, StringBuilder toAppendTo) {
        converter.convert(event.getMessage().getParameters(), toAppendTo);
    }
}

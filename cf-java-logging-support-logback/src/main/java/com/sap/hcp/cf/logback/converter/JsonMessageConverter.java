package com.sap.hcp.cf.logback.converter;

import java.util.List;

import com.sap.hcp.cf.logging.common.converter.DefaultMessageConverter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * A simple {@link ClassicConverter} that converts a message into a JSON
 * message.
 * <p>
 * The main point are that we may need to do escaping and/or flattening
 * depending on the context. <i>Escaping</i> means that we write the message as
 * a quoted string and thus need to <i>escape</i> properly within the message
 * string. If a message is <i>flattened</i>, objects or arrays are turned into a
 * list of fields or values.
 *
 */
public class JsonMessageConverter extends ClassicConverter {

    public static final String WORD = "jsonmsg";
    public static final String OPT_ESCAPE = "escape";
    public static final String OPT_FLATTEN = "flatten";

    private final DefaultMessageConverter converter = new DefaultMessageConverter();

    @Override
    public String convert(ILoggingEvent event) {
        StringBuilder appendTo = new StringBuilder();
        converter.convert(event.getFormattedMessage(), appendTo);
        return appendTo.toString();
    }

    @Override
    public void start() {
        List<String> options = getOptionList();
        if (options != null) {
            for (String option: options) {
                if (OPT_FLATTEN.equalsIgnoreCase(option)) {
                    converter.setFlatten(true);
                } else if (OPT_ESCAPE.equalsIgnoreCase(option)) {
                    converter.setEscape(true);
                }
            }
        }
        super.start();
    }
}

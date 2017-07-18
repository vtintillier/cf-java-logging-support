package com.sap.hcp.cf.logging.common.converter;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONComposer;
import com.fasterxml.jackson.jr.ob.comp.ArrayComposer;

public class DefaultStacktraceConverter {

    private static final int MAX_DEPTH = 42;
    private static String TAB_PATTERN = "\n";
    private static String TAB_SPACES = "  ";
    private static String LS_PROPERTY = "line.separator";
    private static String NEWLINE = "\n";

    private String lineSeparator;
    private final int depth = MAX_DEPTH;

    public DefaultStacktraceConverter() {
        try {
            lineSeparator = System.getProperty(LS_PROPERTY, NEWLINE);
        } catch (Exception ex) {
            lineSeparator = NEWLINE;
        }
    }

    public void convert(Throwable t, StringBuilder appendTo) {
        if (t != null) {
            ArrayComposer<JSONComposer<String>> ac;
            try {
                StringWriter sw = new StringWriter();
                t.printStackTrace(new PrintWriter(sw));
                String[] lines = sw.toString().split(lineSeparator);
                int maxLines = getMaxLines(lines.length);
                ac = JSON.std.composeString().startArray();
                for (int i = 0; i < maxLines; i++) {
                    ac.add(lines[i].trim().replace(TAB_PATTERN, TAB_SPACES));
                }
                appendTo.append(ac.end().finish());
            } catch (Exception ex) {
                /* -- avoids substitute logger warnings on startup -- */
                LoggerFactory.getLogger(DefaultStacktraceConverter.class).error("Conversion failed ", ex);
            }
        }
    }

    private int getMaxLines(int length) {
        return length >= depth ? depth : length;
    }
}

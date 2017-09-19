package com.sap.hcp.cf.logging.common.converter;

import java.io.PrintWriter;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONComposer;
import com.fasterxml.jackson.jr.ob.comp.ArrayComposer;

public class DefaultStacktraceConverter extends StacktraceConverter {
    public static final int MAX_SIZE = 55 * 1024;
    private int maxSize;

    public DefaultStacktraceConverter() {
        this(MAX_SIZE);
    }

    DefaultStacktraceConverter(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void convert(Throwable t, StringBuilder appendTo) {
        if (t == null) {
            return;
        }
        try {
            LineWriter lw = new LineWriter();
            t.printStackTrace(new PrintWriter(lw));
            List<String> lines = lw.getLines();
            StacktraceLines stacktraceLines = new StacktraceLines(lines);

            ArrayComposer<JSONComposer<String>> ac = JSON.std.composeString().startArray();
            if (stacktraceLines.getTotalLineLength() <= maxSize) {
                for (String line: stacktraceLines.getLines()) {
                    ac.add(line);
                }
            } else {
                ac.add("-------- STACK TRACE TRUNCATED --------");
                for (String line: stacktraceLines.getFirstLines(maxSize / 3)) {
                    ac.add(line);
                }
                ac.add("-------- OMITTED --------");
                for (String line: stacktraceLines.getLastLines((maxSize / 3) * 2)) {
                    ac.add(line);
                }
            }
            appendTo.append(ac.end().finish());
        } catch (Exception ex) {
            /* -- avoids substitute logger warnings on startup -- */
            LoggerFactory.getLogger(DefaultStacktraceConverter.class).error("Conversion failed ", ex);
        }
    }
}

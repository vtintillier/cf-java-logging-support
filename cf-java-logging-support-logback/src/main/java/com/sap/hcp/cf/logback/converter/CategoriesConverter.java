package com.sap.hcp.cf.logback.converter;

import java.io.IOException;
import java.util.Iterator;

import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONComposer;
import com.fasterxml.jackson.jr.ob.comp.ArrayComposer;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * This is a simple {@link ClassicConverter} implementation that print the
 * timestamp as a long in nano second resolution.
 */
public class CategoriesConverter extends ClassicConverter {

    public static final String WORD = "categories";

    @Override
    public String convert(ILoggingEvent event) {
        StringBuilder appendTo = new StringBuilder();
        getMarkers(event.getMarker(), appendTo);
        return appendTo.toString();
    }

    @Override
    public void start() {
        super.start();
    }

    private void getMarkers(Marker marker, StringBuilder appendTo) {
        try {
            ArrayComposer<JSONComposer<String>> ac = JSON.std.composeString().startArray();
            getMarkersRecursively(marker, ac);
            appendTo.append(ac.end().finish());
        } catch (IOException ex) {
            LoggerFactory.getLogger(CategoriesConverter.class).error("conversion failed", ex);
        }
    }

    private void getMarkersRecursively(Marker marker, ArrayComposer<JSONComposer<String>> ac) throws IOException {
        if (marker != null) {
            ac.add(marker.getName());
            Iterator it = marker.iterator();
            while (it.hasNext()) {
                getMarkersRecursively((Marker) it.next(), ac);
            }
        }
    }

}

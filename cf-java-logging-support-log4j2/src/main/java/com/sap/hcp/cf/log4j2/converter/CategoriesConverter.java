package com.sap.hcp.cf.log4j2.converter;

import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONComposer;
import com.fasterxml.jackson.jr.ob.comp.ArrayComposer;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

@Plugin(name="CategoriesConverter", category="Converter")
@ConverterKeys({"categories"})
public class CategoriesConverter extends LogEventPatternConverter  {

	public static final String WORD = "categories";

	public CategoriesConverter(String[] options) {
		super(WORD, WORD);
	}
	
	public static CategoriesConverter newInstance(final String[] options) {
		return new CategoriesConverter(options);
	}
	
	@Override
	public void format(LogEvent event, StringBuilder toAppendTo) {
		getMarkers(event.getMarker(), toAppendTo);
	}

	private void getMarkers(Marker marker, StringBuilder toAppendTo) {
		try {
			ArrayComposer<JSONComposer<String>> ac = JSON.std.composeString().startArray();
			getMarkersRecursively(marker, ac);
			toAppendTo.append(ac.end().finish());
		} catch (IOException ex) {
			LoggerFactory.getLogger(CategoriesConverter.class).error("conversion failed", ex);
		}
	}

	private void getMarkersRecursively(Marker marker, ArrayComposer<JSONComposer<String>> ac) throws IOException {
		if (marker != null) {
			ac.add(marker.getName());
			Marker[] parents = marker.getParents();
			if (parents != null) {
				for (Marker parent : parents) {
					getMarkersRecursively(parent, ac);
				}
			}
		}
	}
}

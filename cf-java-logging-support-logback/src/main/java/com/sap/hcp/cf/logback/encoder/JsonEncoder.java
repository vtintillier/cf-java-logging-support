package com.sap.hcp.cf.logback.encoder;

import java.util.HashMap;
import java.util.Map;

import com.sap.hcp.cf.logback.converter.*;
import org.slf4j.Marker;

import com.sap.hcp.cf.logback.encoder.LayoutPatterns.PATTERN_KEY;
import com.sap.hcp.cf.logging.common.Markers;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

/**
 * A {@link LayoutWrappingEncoder} implementation that encodes an {@link ILoggingEvent} as a JSON object.
 * <p>
 * Under the hood, it's using a couple of {@link PatternLayout} instances to support different logging
 * contexts. These patterns are specified in {@link LayoutPatterns}, so there's no need/way to do that in 
 * the logback configuration file. There, you should only specify that you use this encoder with an appender, for example like this:
 * <blockquote><pre>
 * &lt;appender name="STDOUT-JSON" class="ch.qos.logback.core.ConsoleAppender"&gt;
 *    &lt;encoder class="com.sap.hcp.cf.logback.encoder.JsonEncoder"/&gt;
 * &lt;/appender&gt;
 * </pre>
 * </blockquote>
 */
public class JsonEncoder extends LayoutWrappingEncoder<ILoggingEvent> {

	public static class JsonLayout extends LayoutBase<ILoggingEvent> {
	
		private final Map<Marker, PatternLayout> layouts = new HashMap<Marker, PatternLayout>();
				
		@Override
		public void start() {
			defineConverters();
			initPatterns();
			super.start();
		}

		public String doLayout(ILoggingEvent event) {
			return getLayout(event).doLayout(event);
		}
		
		private PatternLayout getLayout(ILoggingEvent event) {
			PatternLayout layout = layouts.get(getMarker(event));
			if (layout == null) {
				layout = layouts.get(Markers.DEFAULT_MARKER);
			}
			return layout;
		}
		
		private Marker getMarker(ILoggingEvent event) {
			if (hasException(event)) {
				return Markers.EXCEPTION_MARKER;
			}
			Marker m = event.getMarker();
			if (m == null) {
				m = Markers.DEFAULT_MARKER;
			}
			return m;
		}
		
		private boolean hasException(ILoggingEvent event) {
			return event.getThrowableProxy() != null;
		}

		private void defineConverters() {
			PatternLayout.defaultConverterMap.put(ArgsConverter.WORD, ArgsConverter.class.getName());
			PatternLayout.defaultConverterMap.put(JsonMessageConverter.WORD, JsonMessageConverter.class.getName());
			PatternLayout.defaultConverterMap.put(ContextPropsConverter.WORD, ContextPropsConverter.class.getName());			
			PatternLayout.defaultConverterMap.put(StacktraceConverter.WORD, StacktraceConverter.class.getName());
			PatternLayout.defaultConverterMap.put(TimestampConverter.WORD, TimestampConverter.class.getName());
			PatternLayout.defaultConverterMap.put(CategoriesConverter.WORD, CategoriesConverter.class.getName());
		}
		
		private void initPatterns() {
			PatternLayout pl = new PatternLayout();
			pl.setPattern(LayoutPatterns.getPattern(PATTERN_KEY.APPLICATION));
			pl.setContext(context);
			pl.start();
			layouts.put(Markers.DEFAULT_MARKER, pl);
			
			pl = new PatternLayout();
			pl.setPattern(LayoutPatterns.getPattern(PATTERN_KEY.EXCEPTION));
			pl.setContext(context);
			pl.start();
			layouts.put(Markers.EXCEPTION_MARKER, pl);
			
			pl = new PatternLayout();
			pl.setPattern(LayoutPatterns.getPattern(PATTERN_KEY.REQUEST));
			pl.setContext(context);
			pl.start();
			layouts.put(Markers.REQUEST_MARKER, pl);			
		}
	}
	
	@Override
	public void start() {
		JsonLayout jsonLayout = new JsonLayout();
	    jsonLayout.setContext(context);
	    jsonLayout.start();

		this.layout = jsonLayout;
		super.start();
	}
}

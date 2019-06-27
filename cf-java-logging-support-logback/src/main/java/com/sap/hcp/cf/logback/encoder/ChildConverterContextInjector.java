package com.sap.hcp.cf.logback.encoder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.CompositeConverter;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.PostCompileProcessor;
import ch.qos.logback.core.spi.ContextAware;

public class ChildConverterContextInjector implements PostCompileProcessor<ILoggingEvent> {

	@Override
	public void process(Context context, Converter<ILoggingEvent> head) {
		inject(context, head, false);
	}

	private void inject(Context context, Converter<ILoggingEvent> head, boolean injectAll) {
		for (Converter<ILoggingEvent> c = head; c != null; c = c.getNext()) {
			if (c instanceof CompositeConverter<?>) {
				Converter<ILoggingEvent> childConverter = ((CompositeConverter<ILoggingEvent>) c).getChildConverter();
				inject(context, childConverter, true);
			}
			if (injectAll && c instanceof ContextAware) {
				((ContextAware) c).setContext(context);
			}
		}
	}

}

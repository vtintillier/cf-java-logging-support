package com.sap.hcp.cf.logback.encoder;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.mockito.Mockito;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.pattern.CompositeConverter;
import ch.qos.logback.core.pattern.Converter;
import ch.qos.logback.core.pattern.DynamicConverter;
import ch.qos.logback.core.pattern.PostCompileProcessor;

public class ChildConverterContextInjectorTest {

	private static class SimpleTestConverter extends Converter<ILoggingEvent> {

		@Override
		public String convert(ILoggingEvent event) {
			return null;
		}
	}
	
	private static class ContextAwareTestConverter extends DynamicConverter<ILoggingEvent> {

		@Override
		public String convert(ILoggingEvent event) {
			return null;
		}
	}

	private static class CompositeTestConverter extends CompositeConverter<ILoggingEvent> {

		@Override
		protected String transform(ILoggingEvent event, String in) {
			return null;
		}
	}

	private final PostCompileProcessor<ILoggingEvent> processor = new ChildConverterContextInjector();

	@Test
	public void doesNotInjectContextInSingleConverter() throws Exception {
		DynamicConverter<ILoggingEvent> converter = new ContextAwareTestConverter();

		processor.process(Mockito.mock(Context.class), converter);

		assertNull("No context should be set.", converter.getContext());
	}

	@Test
	public void injectsContextInSingleChild() throws Exception {
		CompositeTestConverter parent = new CompositeTestConverter();
		ContextAwareTestConverter child = new ContextAwareTestConverter();
		parent.setChildConverter(child);
		Context context = Mockito.mock(Context.class);

		processor.process(context, parent);

		assertThat(child.getContext(), is(sameInstance(context)));
	}

	@Test
	public void followsChainToConverterWithChild() throws Exception {
		SimpleTestConverter start = new SimpleTestConverter();
		CompositeTestConverter parent = new CompositeTestConverter();
		ContextAwareTestConverter child = new ContextAwareTestConverter();
		start.setNext(parent);
		parent.setChildConverter(child);
		Context context = Mockito.mock(Context.class);

		processor.process(context, parent);

		assertThat(child.getContext(), is(sameInstance(context)));
	}

	@Test
	public void injectsIntoChildrenOfChildren() throws Exception {
		CompositeTestConverter grandParent = new CompositeTestConverter();
		CompositeTestConverter parent = new CompositeTestConverter();
		ContextAwareTestConverter child = new ContextAwareTestConverter();
		grandParent.setChildConverter(parent);
		parent.setChildConverter(child);
		Context context = Mockito.mock(Context.class);

		processor.process(context, grandParent);

		assertThat(parent.getContext(), is(sameInstance(context)));
		assertThat(child.getContext(), is(sameInstance(context)));
	}

	@Test
	public void injectsIntoFullChildChain() throws Exception {
		CompositeTestConverter parent = new CompositeTestConverter();
		SimpleTestConverter child1 = new SimpleTestConverter();
		ContextAwareTestConverter child2 = new ContextAwareTestConverter();
		parent.setChildConverter(child1);
		child1.setNext(child2);
		Context context = Mockito.mock(Context.class);

		processor.process(context, parent);

		assertThat(child2.getContext(), is(sameInstance(context)));

	}
}

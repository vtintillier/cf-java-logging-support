package com.sap.hcp.cf.log4j2.layout;

import java.nio.charset.Charset;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.MarkerPatternSelector;
import org.apache.logging.log4j.core.layout.PatternMatch;
import org.apache.logging.log4j.core.layout.PatternSelector;
import org.apache.logging.log4j.core.pattern.PatternFormatter;

import com.sap.hcp.cf.log4j2.layout.LayoutPatterns.PATTERN_KEY;
import com.sap.hcp.cf.logging.common.Markers;

@Plugin(name = "JsonPatternLayout", category = "Core", elementType = "Layout", printObject = true)
public final class JsonPatternLayout extends AbstractStringLayout {

    private static final long serialVersionUID = 1L;

    private final PatternSelector markerSelector;
    private final PatternSelector execptionSelector;

    private final Configuration config;

    protected JsonPatternLayout(Configuration config, Charset charset) {
        super(charset);
        this.config = config;
        markerSelector = createPatternSelector();
        execptionSelector = createExceptionSelector();
    }

    @Override
    public String toSerializable(LogEvent event) {
        PatternSelector selector = getSelector(event);
        final StringBuilder buf = getStringBuilder();
        PatternFormatter[] formatters = selector.getFormatters(event);
        final int len = formatters.length;
        for (int i = 0; i < len; i++) {
            formatters[i].format(event, buf);
        }
        String str = buf.toString();
        return str;
    }

    @PluginFactory
    public static JsonPatternLayout createLayout(@PluginAttribute(value = "charset") final Charset charset,
                                                 @PluginConfiguration final Configuration config) {
        return new Builder().withCharset(charset).withConfiguration(config).build();
    }

    public static class Builder implements org.apache.logging.log4j.core.util.Builder<JsonPatternLayout> {

        @PluginBuilderAttribute
        private Charset charset = Charset.defaultCharset();

        @PluginConfiguration
        private Configuration configuration = null;

        private Builder() {
        }

        public Builder withCharset(final Charset charset) {
            if (charset != null) {
                this.charset = charset;
            }
            return this;
        }

        public Builder withConfiguration(final Configuration configuration) {
            this.configuration = configuration;
            return this;
        }

        @Override
        public JsonPatternLayout build() {
            return new JsonPatternLayout(configuration, charset);
        }
    }

    private MarkerPatternSelector createPatternSelector() {
        PatternMatch[] pMatches = new PatternMatch[1];
        pMatches[0] = new PatternMatch(Markers.REQUEST_MARKER.getName(), LayoutPatterns.getPattern(
                                                                                                   PATTERN_KEY.REQUEST));

        return new MarkerPatternSelector(pMatches, LayoutPatterns.getPattern(PATTERN_KEY.APPLICATION), false, false,
                                         config);
    }

    private PatternSelector createExceptionSelector() {
        return new MarkerPatternSelector(new PatternMatch[0], LayoutPatterns.getPattern(PATTERN_KEY.EXCEPTION), false,
                                         false, config);
    }

    private PatternSelector getSelector(LogEvent event) {
        if (event.getThrownProxy() != null || event.getThrown() != null) {
            return execptionSelector;
        }
        return markerSelector;
    }
}

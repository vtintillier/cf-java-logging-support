package com.sap.cloud.cf.monitoring.client.configuration;

import static com.sap.cloud.cf.monitoring.client.configuration.CustomMetricsConfiguration.DEFAULT_INTERVAL;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

final class LongIntervalGsonTypeAdapter implements JsonDeserializer<Long> {
    private static final Logger LOGGER = LoggerFactory.getLogger(LongIntervalGsonTypeAdapter.class);
    private static final long MINIMAL_INTERVAL = 20000L;

    @Override
    public Long deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
        long value = Long.parseLong(json.getAsString());
        if (value < MINIMAL_INTERVAL) {
            LOGGER.warn(String.format(
                "The value of 'interval' property could not be less than %s. The default value %s will be used.",
                MINIMAL_INTERVAL, DEFAULT_INTERVAL));
            return DEFAULT_INTERVAL;
        }
        return value;
    }
}
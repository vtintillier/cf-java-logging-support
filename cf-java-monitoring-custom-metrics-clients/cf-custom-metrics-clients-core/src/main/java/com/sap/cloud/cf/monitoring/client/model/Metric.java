package com.sap.cloud.cf.monitoring.client.model;

import static com.sap.cloud.cf.monitoring.client.utils.Utils.checkNotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Metric implements Serializable {

    private static final long serialVersionUID = 1821203758427351658L;

    private String name;
    private double value;
    private long timestamp;
    private Map<String, String> tags;

    public Metric(String name, double value, long timestamp) {
        this(name, value, timestamp, new HashMap<String, String>());
    }

    public Metric(String name, double value, long timestamp, Map<String, String> tags) {
        checkNotNull(name, "name");
        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        checkNotNull(value, "value");
        checkNotNull(timestamp, "timestamp");
        checkNotNull(tags, "tags");
        this.name = name;
        this.value = value;
        this.timestamp = timestamp;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getTags() {
        return new HashMap<String, String>(tags);
    }

    public String getTag(String key) {
        return tags.get(key);
    }

    public synchronized String addTag(String key, String value) {
        if (tags == null) {
            tags = new HashMap<String, String>();
        }
        return tags.put(key, value);
    }

    @Override
    public String toString() {
        return new StringBuilder("Metric[").append("name=")
            .append(name)
            .append(", value=")
            .append(value)
            .append(", timestamp=")
            .append(timestamp)
            .append(", tags=")
            .append(tags)
            .append("]")
            .toString();
    }

}

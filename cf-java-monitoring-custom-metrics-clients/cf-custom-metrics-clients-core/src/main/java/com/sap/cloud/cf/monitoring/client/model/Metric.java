package com.sap.cloud.cf.monitoring.client.model;

import static com.sap.cloud.cf.monitoring.client.utils.Utils.checkNotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Metric implements Serializable {

    private static final long serialVersionUID = 1821203758427351658L;

    private final String name;
    private final double value;
    private final long timestamp;
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
        return new StringBuilder("Metric[").append("name=").append(name).append(", value=").append(value).append(
                                                                                                                 ", timestamp=")
                                           .append(timestamp).append(", tags=").append(tags).append("]").toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (tags == null ? 0 : tags.hashCode());
        result = prime * result + (int) (timestamp ^ timestamp >>> 32);
        long temp;
        temp = Double.doubleToLongBits(value);
        result = prime * result + (int) (temp ^ temp >>> 32);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Metric other = (Metric) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (tags == null) {
            if (other.tags != null) {
                return false;
            }
        } else if (!tags.equals(other.tags)) {
            return false;
        }
        if (timestamp != other.timestamp) {
            return false;
        }
        if (Double.doubleToLongBits(value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        return true;
    }

}

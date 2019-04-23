package com.sap.cloud.cf.monitoring.client.utils;

public class Utils {

    public static void checkNotNull(Object object, String definition) {
        if (object == null) {
            throw new IllegalArgumentException(String.format("No %s provided", definition));
        }
    }
}

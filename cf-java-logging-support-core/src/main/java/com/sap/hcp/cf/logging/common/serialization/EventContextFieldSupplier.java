package com.sap.hcp.cf.logging.common.serialization;

import java.util.Map;

@FunctionalInterface
public interface EventContextFieldSupplier<T> extends ContextFieldSupplier {

    Map<String, Object> map(T event);

    default Map<String, Object> get() {
        return map(null);
    }
}

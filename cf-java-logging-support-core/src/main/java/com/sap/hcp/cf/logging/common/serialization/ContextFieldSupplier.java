package com.sap.hcp.cf.logging.common.serialization;

import java.util.Map;
import java.util.function.Supplier;

@FunctionalInterface
public interface ContextFieldSupplier extends Supplier<Map<String, Object>> {
}

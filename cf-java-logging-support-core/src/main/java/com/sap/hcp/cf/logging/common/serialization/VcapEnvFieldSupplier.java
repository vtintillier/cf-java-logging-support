package com.sap.hcp.cf.logging.common.serialization;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.sap.hcp.cf.logging.common.LogContext;
import com.sap.hcp.cf.logging.common.VcapEnvReader;

public class VcapEnvFieldSupplier implements ContextFieldSupplier {

    private final Map<String, Object> envMap;

    public VcapEnvFieldSupplier() {
        this(false);
    }

    protected VcapEnvFieldSupplier(boolean sendDefaultValues) {
        Map<String, Object> map = new HashMap<>();
        map.putAll(VcapEnvReader.getEnvMap());
        if (sendDefaultValues) {
            LogContext.getContextFieldsKeys().stream().forEach(k -> map.computeIfAbsent(k, LogContext::getDefault));
        }
        this.envMap = Collections.unmodifiableMap(map);
    }

    @Override
    public Map<String, Object> get() {
        return envMap;
    }

}

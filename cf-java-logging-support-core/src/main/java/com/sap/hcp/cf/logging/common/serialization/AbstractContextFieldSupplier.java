package com.sap.hcp.cf.logging.common.serialization;

import java.util.HashMap;
import java.util.Map;

import com.sap.hcp.cf.logging.common.customfields.CustomField;

public abstract class AbstractContextFieldSupplier<T> implements EventContextFieldSupplier<T> {

    public AbstractContextFieldSupplier() {
        super();
    }

    @Override
    public Map<String, Object> map(T event) {
        Map<String, Object> result = new HashMap<>();
        result.putAll(getContextMap(event));
        Object[] parameters = getParameterArray(event);
        if (parameters == null) {
            return result;
        }
        for (Object parameter: parameters) {
            if (parameter instanceof CustomField) {
                CustomField customField = (CustomField) parameter;
                result.put(customField.getKey(), customField.getValue());
            }
        }
        return result;
    }

    protected abstract Object[] getParameterArray(T event);

    protected abstract Map<? extends String, ? extends Object> getContextMap(T event);

}

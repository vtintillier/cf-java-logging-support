package com.sap.hcp.cf.sample.jersey;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.sap.hcp.cf.logging.jersey.filter.RequestMetricsDynamicBinding;

public class SampleApp extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> result = new HashSet<Class<?>>();
        result.add(Sample.class);
        result.add(RequestMetricsDynamicBinding.class);
        return result;
    }
}

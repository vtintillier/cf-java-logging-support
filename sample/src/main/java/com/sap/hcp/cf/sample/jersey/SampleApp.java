package com.sap.hcp.cf.sample.jersey;

import com.sap.hcp.cf.logging.jersey.filter.RequestMetricsDynamicBinding;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class SampleApp extends Application {
  @Override
  public Set<Class<?>> getClasses() {
    Set<Class<?>> result = new HashSet<Class<?>>();
    result.add(Sample.class);
    result.add(RequestMetricsDynamicBinding.class);
    return result;
  }
}

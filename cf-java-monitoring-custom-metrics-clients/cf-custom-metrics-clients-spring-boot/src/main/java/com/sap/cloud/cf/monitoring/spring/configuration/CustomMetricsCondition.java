package com.sap.cloud.cf.monitoring.spring.configuration;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class CustomMetricsCondition implements Condition {

    private static final String VCAP_SERVICES_KEY = "VCAP_SERVICES";
    private static final Logger LOG = LoggerFactory.getLogger(CustomMetricsCondition.class);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        Optional<VcapServices> vcapServices = Optional.of(context).map(ConditionContext::getEnvironment).map(env -> env
                                                                                                                       .getProperty(VCAP_SERVICES_KEY))
                                                      .map(s -> new Gson().fromJson(s, VcapServices.class));
        if (!vcapServices.isPresent()) {
            LOG.error("Custom Metrics reporter will not start since required environment variable ''{}'' is missing.",
                      VCAP_SERVICES_KEY);
            return false;
        }
        boolean isBoundToApplicationLogging = vcapServices.map(VcapServices::getApplicationLogs).map(l -> !l.isEmpty())
                                                          .orElse(false);
        if (!isBoundToApplicationLogging) {
            LOG.error("Custom Metrics reporter will not start since app is not bound to application-logging.",
                      VCAP_SERVICES_KEY);

        }
        return isBoundToApplicationLogging;
    }

    private static class VcapServices {
        @SerializedName("application-logs")
        private List<ApplicationLogs> applicationLogs;

        public List<ApplicationLogs> getApplicationLogs() {
            return applicationLogs;
        }
    }

    private static class ApplicationLogs {
    }

}

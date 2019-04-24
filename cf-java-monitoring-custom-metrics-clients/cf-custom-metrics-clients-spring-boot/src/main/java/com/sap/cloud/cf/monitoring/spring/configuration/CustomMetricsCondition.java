package com.sap.cloud.cf.monitoring.spring.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.sap.cloud.cf.monitoring.client.configuration.CFConfigurationProvider;

public class CustomMetricsCondition implements Condition {

    private static final Logger LOG = LoggerFactory.getLogger(CustomMetricsCondition.class);

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        try {
            new CFConfigurationProvider();
        } catch (IllegalArgumentException ex) {
            LOG.error("Custom Metrics reporter will not start since required ENVs are missing: {}", ex.getMessage());
            return false;
        }
        return true;
    }
}
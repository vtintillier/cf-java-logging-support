package com.sap.cloud.cf.monitoring.spring.configuration;

import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import com.sap.cloud.cf.monitoring.client.MonitoringClient;
import com.sap.cloud.cf.monitoring.client.MonitoringClientBuilder;
import com.sap.cloud.cf.monitoring.client.configuration.CustomMetricsConfiguration;
import com.sap.cloud.cf.monitoring.client.configuration.CustomMetricsConfigurationFactory;
import com.sap.cloud.cf.monitoring.spring.CustomMetricWriter;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;

@AutoConfigureBefore(MetricsAutoConfiguration.class)
@ConditionalOnClass(MeterRegistry.class)
@Configuration
@Conditional(CustomMetricsCondition.class)
public class CustomMetricsAutoConfiguration {
    @Bean
    CustomMetricWriter metricWriter(Clock clock, MonitoringClient client) {
        CustomMetricsConfiguration config = CustomMetricsConfigurationFactory.create();
        return new CustomMetricWriter(config, clock, client);
    }

    @Bean
    MonitoringClient metricPublisher() {
		return new MonitoringClientBuilder().create();
    }

}

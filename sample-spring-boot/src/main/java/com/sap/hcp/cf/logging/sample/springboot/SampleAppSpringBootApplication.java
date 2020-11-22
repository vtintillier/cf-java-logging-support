package com.sap.hcp.cf.logging.sample.springboot;

import java.time.Clock;

import javax.servlet.DispatcherType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.sap.hcp.cf.logging.sample.springboot.keystore.KeyStoreDynLogConfiguration;
import com.sap.hcp.cf.logging.servlet.dynlog.DynLogConfiguration;
import com.sap.hcp.cf.logging.servlet.filter.RequestLoggingFilter;

@SpringBootApplication
@EnableWebMvc
public class SampleAppSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleAppSpringBootApplication.class, args);
	}

	/**
	 * Registers a customized {@link RequestLoggingFilter} with the servlet.
	 * We inject our own dynamic logging configuration, that contains the public RSA key from our keystore.
	 * 
	 * @param dynLogConfig autowired with {@link KeyStoreDynLogConfiguration}
	 * @return a registration of the {@link RequestLoggingFilter}
	 */
	@Bean
	public FilterRegistrationBean<RequestLoggingFilter> loggingFilter(@Autowired DynLogConfiguration dynLogConfig) {
		FilterRegistrationBean<RequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestLoggingFilter(() -> dynLogConfig));
		registrationBean.setName("request-logging");
		registrationBean.addUrlPatterns("/*");
		registrationBean.setDispatcherTypes(DispatcherType.REQUEST);
		return registrationBean;
	}

	/**
	 * Provides a global {@link Clock} instance. Useful for testing.
	 * @return the global clock
	 */
	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}
}

package com.sap.hcp.cf.logging.sample.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import com.sap.hcp.cf.logging.sample.springboot.config.BasicAuthenticationConfiguration;

@Configuration
@EnableWebSecurity
public class SampleAppWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

	private final BasicAuthenticationConfiguration config;
	private final BasicAuthenticationEntryPoint entryPoint;
	private final BCryptPasswordEncoder passwordEncoder;

	public SampleAppWebSecurityConfigurerAdapter(@Autowired BasicAuthenticationConfiguration config,
			@Autowired BasicAuthenticationEntryPoint entryPoint, @Autowired BCryptPasswordEncoder passwordEncoder) {
		this.config = config;
		this.entryPoint = entryPoint;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Require basic authentication for all endpoints.
	 */
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		String password = passwordEncoder.encode(config.getPassword());
		auth.inMemoryAuthentication().withUser(config.getUsername()).password(password).roles(Roles.USER);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic()
				.authenticationEntryPoint(entryPoint);
	}
}

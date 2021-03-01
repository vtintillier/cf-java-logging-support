package com.sap.hcp.cf.logging.sample.springboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "keystore.token")
public class KeyStoreConfiguration {

	private String location;
	private String type;
	private char[] password;
	private String keyAlias;
	private char[] keyPassword;

	public String getType() {
		return type;
	}

	public String getLocation() {
		return location;
	}

	public char[] getPassword() {
		return password;
	}

	public String getKeyAlias() {
		return keyAlias;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setPassword(char[] password) {
		this.password = password;
	}

	public void setKeyAlias(String keyAlias) {
		this.keyAlias = keyAlias;
	}

	public void setKeyPassword(char[] keyPassword) {
		this.keyPassword = keyPassword;
	}

	public char[] getKeyPassword() {
		return keyPassword;
	}

}

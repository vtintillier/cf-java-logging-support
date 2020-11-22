package com.sap.hcp.cf.logging.sample.springboot.keystore;

import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sap.hcp.cf.logging.servlet.dynlog.DynLogConfiguration;

/**
 * Provides the public key for JWT validation out of the keystore.
 * Otherwise adheres to the configuration by environment variables.
 *
 */
@Component
public class KeyStoreDynLogConfiguration implements DynLogConfiguration {

	private TokenKeyProvider keyProvider;
	private String dynLogHeaderKey;

	public KeyStoreDynLogConfiguration(@Autowired TokenKeyProvider keyProvider,
			@Value("${DYN_LOG_HEADER:SAP-LOG-LEVEL}") String dynLogHeaderKey) {
		this.keyProvider = keyProvider;
		this.dynLogHeaderKey = dynLogHeaderKey;
	}

	@Override
	public String getDynLogHeaderKey() {
		return dynLogHeaderKey;
	}

	@Override
	public RSAPublicKey getRsaPublicKey() {
		String keyId = keyProvider.getPrivateKeyId();
		return keyProvider.getPublicKeyById(keyId);
	}

}

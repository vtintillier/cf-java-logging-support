package com.sap.hcp.cf.logging.servlet.dynlog;

import static org.junit.Assert.assertEquals;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.auth0.jwt.interfaces.DecodedJWT;

public class TokenDecoderTest {

    private String token;
    private KeyPair validKeyPair;
    private KeyPair invalidKeyPair;

    @Before
    public void setup() throws NoSuchAlgorithmException, NoSuchProviderException, DynamicLogLevelException {
        validKeyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        invalidKeyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        Date issuedAt = new Date();
        Date expiresAt = new Date(new Date().getTime() + 10000);
        token = TokenCreator.createToken(validKeyPair, "issuer", issuedAt, expiresAt, "TRACE");
    }

    @Test
    public void testTokenContent() throws Exception {
        TokenDecoder tokenDecoder = new TokenDecoder((RSAPublicKey) validKeyPair.getPublic());
        DecodedJWT jwt = tokenDecoder.validateAndDecodeToken(token);
        assertEquals(jwt.getClaim("level").asString(), "TRACE");
    }

    @Test(expected = DynamicLogLevelException.class)
    public void testInvalidPublicKey() throws Exception {
        TokenDecoder tokenDecoder = new TokenDecoder((RSAPublicKey) invalidKeyPair.getPublic());
        tokenDecoder.validateAndDecodeToken(token);
    }
}

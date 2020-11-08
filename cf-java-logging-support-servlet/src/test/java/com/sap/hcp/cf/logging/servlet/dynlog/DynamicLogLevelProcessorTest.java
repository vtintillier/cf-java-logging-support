package com.sap.hcp.cf.logging.servlet.dynlog;

import static org.junit.Assert.assertEquals;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;

import javax.xml.bind.DatatypeConverter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.MDC;

import com.sap.hcp.cf.logging.common.helper.DynamicLogLevelHelper;
import com.sap.hcp.cf.logging.common.helper.Environment;

@RunWith(MockitoJUnitRunner.class)
public class DynamicLogLevelProcessorTest extends Mockito {

    @Mock
    private Environment environment;

    private DynamicLogLevelProcessor processor;

    private String token;

    @Before
    public void setup() throws NoSuchAlgorithmException, NoSuchProviderException, DynamicLogLevelException {
        KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
        String keyBase64 = DatatypeConverter.printBase64Binary(keyPair.getPublic().getEncoded());
        Date issuedAt = new Date();
        Date expiresAt = new Date(new Date().getTime() + 10000);
		this.token = TokenCreator.createToken(keyPair, "issuer", issuedAt, expiresAt, "TRACE", "myPrefix");
        when(environment.getVariable("DYN_LOG_LEVEL_KEY")).thenReturn(keyBase64);
        when(environment.getVariable("DYN_LOG_HEADER")).thenReturn("SAP-LOG-LEVEL");
        processor = new DynamicLogLevelProcessor(new DynLogEnvironment(environment));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testCopyDynamicLogLevelToMDC() throws Exception {
        processor.copyDynamicLogLevelToMDC(token);
        assertEquals("TRACE", MDC.get(DynamicLogLevelHelper.MDC_DYNAMIC_LOG_LEVEL_KEY));
    }

    @Test
    public void testDeleteDynamicLogLevelFromMDC() throws Exception {
        processor.copyDynamicLogLevelToMDC(token);
        processor.removeDynamicLogLevelFromMDC();
        assertEquals(null, MDC.get(DynamicLogLevelHelper.MDC_DYNAMIC_LOG_LEVEL_KEY));
    }

	@Test
	public void testCopyDynamicLogPackagesToMDC() throws Exception {
		processor.copyDynamicLogLevelToMDC(token);
		assertEquals("myPrefix", MDC.get(DynamicLogLevelHelper.MDC_DYNAMIC_LOG_LEVEL_PREFIXES));

	}
}

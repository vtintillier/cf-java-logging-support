package com.sap.hcp.cf.logging.common;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.sap.hcp.cf.logging.common.helper.Environment;

public class LogOptionalFieldsSettingsTest {

    @Test
    public void testLogOptionalFieldsSettingsTrue() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_SENSITIVE_CONNECTION_DATA)).thenReturn("true");
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_USER)).thenReturn("True");
        when(mockEnvironment.getVariable(Environment.LOG_REFERER)).thenReturn("TRUE");

        LogOptionalFieldsSettings settings = new LogOptionalFieldsSettings(mockEnvironment, "NameOfInvokingClass");

        assertTrue("Wrapping LOG_SENSITIVE_CONNECTION_DATA failed", settings.isLogSensitiveConnectionData());
        assertTrue("Wrapping LOG_REMOTE_USER failed", settings.isLogRemoteUserField());
        assertTrue("Wrapping LOG_REFERER failed", settings.isLogRefererField());
    }

    @Test
    public void testLogOptionalFieldsSettingsFalse() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_SENSITIVE_CONNECTION_DATA)).thenReturn("false");
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_USER)).thenReturn("False");
        when(mockEnvironment.getVariable(Environment.LOG_REFERER)).thenReturn("FALSE");

        LogOptionalFieldsSettings settings = new LogOptionalFieldsSettings(mockEnvironment, "NameOfInvokingClass");

        assertFalse("Wrapping LOG_SENSITIVE_CONNECTION_DATA failed", settings.isLogSensitiveConnectionData());
        assertFalse("Wrapping LOG_REMOTE_USER failed", settings.isLogRemoteUserField());
        assertFalse("Wrapping LOG_REFERER failed", settings.isLogRefererField());
    }

    @Test
    public void testLogOptionalFieldsSettingsInvalidEnvVariable() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_SENSITIVE_CONNECTION_DATA)).thenReturn("someInvalidString");
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_USER)).thenReturn("someInvalidString");
        when(mockEnvironment.getVariable(Environment.LOG_REFERER)).thenReturn("someInvalidString");

        LogOptionalFieldsSettings settings = new LogOptionalFieldsSettings(mockEnvironment, "NameOfInvokingClass");
        assertFalse("Wrapping LOG_SENSITIVE_CONNECTION_DATA failed", settings.isLogSensitiveConnectionData());
        assertFalse("Wrapping LOG_REMOTE_USER failed", settings.isLogRemoteUserField());
        assertFalse("Wrapping LOG_REFERER failed", settings.isLogRefererField());
    }

    @Test
    public void testLogOptionalFieldsSettingsEmptyString() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_SENSITIVE_CONNECTION_DATA)).thenReturn("");
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_USER)).thenReturn("");
        when(mockEnvironment.getVariable(Environment.LOG_REFERER)).thenReturn("");

        LogOptionalFieldsSettings settings = new LogOptionalFieldsSettings(mockEnvironment, "NameOfInvokingClass");
        assertFalse("Wrapping LOG_SENSITIVE_CONNECTION_DATA failed", settings.isLogSensitiveConnectionData());
        assertFalse("Wrapping LOG_REMOTE_USER failed", settings.isLogRemoteUserField());
        assertFalse("Wrapping LOG_REFERER failed", settings.isLogRefererField());
    }

    @Test
    public void testLogOptionalFieldsSettingsEmptyEnvVariable() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_SENSITIVE_CONNECTION_DATA)).thenReturn(null);
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_USER)).thenReturn(null);
        when(mockEnvironment.getVariable(Environment.LOG_REFERER)).thenReturn(null);

        LogOptionalFieldsSettings settings = new LogOptionalFieldsSettings(mockEnvironment, "NameOfInvokingClass");

        assertFalse("Wrapping LOG_SENSITIVE_CONNECTION_DATA failed", settings.isLogSensitiveConnectionData());
        assertFalse("Wrapping LOG_REMOTE_USER failed", settings.isLogRemoteUserField());
        assertFalse("Wrapping LOG_REFERER failed", settings.isLogRefererField());
    }

    @Test
    public void testLogOptionalFieldsWithMixedSettings() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_SENSITIVE_CONNECTION_DATA)).thenReturn("false");
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_USER)).thenReturn("true");
        when(mockEnvironment.getVariable(Environment.LOG_REFERER)).thenReturn("True");

        LogOptionalFieldsSettings settings = new LogOptionalFieldsSettings(mockEnvironment, "NameOfInvokingClass");

        assertFalse("Wrapping LOG_SENSITIVE_CONNECTION_DATA failed", settings.isLogSensitiveConnectionData());
        assertTrue("Wrapping LOG_REMOTE_USER failed", settings.isLogRemoteUserField());
        assertTrue("Wrapping LOG_REFERER failed", settings.isLogRefererField());
    }

}

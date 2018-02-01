package com.sap.hcp.cf.logging.servlet.filter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.sap.hcp.cf.logging.common.helper.Environment;

public class LogRemoteIPSettingsTest {

    @Test
    public void testLogRemoteIPSettingsTrue() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_IP)).thenReturn("true");
        assertEquals(new LogRemoteIPSettings(mockEnvironment).getLogRemoteIPSetting(), true);
    }

    @Test
    public void testLogRemoteIPSettingsTrueWithCapitalT() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_IP)).thenReturn("True");
        assertEquals(new LogRemoteIPSettings(mockEnvironment).getLogRemoteIPSetting(), true);
    }

    @Test
    public void testLogRemoteIPSettingsTRUE() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_IP)).thenReturn("TRUE");
        assertEquals(new LogRemoteIPSettings(mockEnvironment).getLogRemoteIPSetting(), true);
    }

    @Test
    public void testLogRemoteIPSettingsFalse() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_IP)).thenReturn("false");
        assertEquals(new LogRemoteIPSettings(mockEnvironment).getLogRemoteIPSetting(), false);
    }

    @Test
    public void testLogRemoteIPSettingsFalseWithCapitalF() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_IP)).thenReturn("False");
        assertEquals(new LogRemoteIPSettings(mockEnvironment).getLogRemoteIPSetting(), false);
    }

    @Test
    public void testLogRemoteIPSettingsFALSE() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_IP)).thenReturn("FALSE");
        assertEquals(new LogRemoteIPSettings(mockEnvironment).getLogRemoteIPSetting(), false);
    }

    @Test
    public void testLogRemoteIPSettingsInvalidEnvVariable() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_IP)).thenReturn("someInvalidString");
        assertEquals(new LogRemoteIPSettings(mockEnvironment).getLogRemoteIPSetting(), false);
    }

    @Test
    public void testLogRemoteIPSettingsEmptyEnvVariable() {
        Environment mockEnvironment = mock(Environment.class);
        when(mockEnvironment.getVariable(Environment.LOG_REMOTE_IP)).thenReturn(null);
        assertEquals(new LogRemoteIPSettings(mockEnvironment).getLogRemoteIPSetting(), false);
    }
}

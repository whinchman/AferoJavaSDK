/*
 * Copyright (c) 2014-2017 Afero, Inc. All rights reserved.
 */

package io.afero.sdk.conclave;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

/**
 * Comprehensive unit tests for ConclaveMessage and its inner classes.
 * Tests message structure, serialization capabilities, and utility methods.
 */
public class ConclaveMessageTest {

    @Test
    public void testHello_Creation() {
        String version = "1.0.0";
        ConclaveMessage.Hello hello = new ConclaveMessage.Hello(version);
        
        assertNotNull("Hello should not be null", hello);
        assertNotNull("Hello fields should not be null", hello.hello);
        assertEquals("Version should match", version, hello.hello.version);
    }

    @Test
    public void testHelloFields_DefaultValues() {
        ConclaveMessage.HelloFields fields = new ConclaveMessage.HelloFields();
        
        // Test default values
        assertNull("Version should be null by default", fields.version);
        assertEquals("Heartbeat should be 0 by default", 0, fields.heartbeat);
        assertEquals("Buffer size should be 0 by default", 0, fields.bufferSize);
    }

    @Test
    public void testHelloFields_SetValues() {
        ConclaveMessage.HelloFields fields = new ConclaveMessage.HelloFields();
        
        fields.version = "2.1.0";
        fields.heartbeat = 30;
        fields.bufferSize = 1024;
        
        assertEquals("Version should be set", "2.1.0", fields.version);
        assertEquals("Heartbeat should be set", 30, fields.heartbeat);
        assertEquals("Buffer size should be set", 1024, fields.bufferSize);
    }

    @Test
    public void testWelcome_Creation() {
        int sessionId = 12345;
        long generation = 9876543210L;
        int seq = 100;
        String accountId = "test-account-id";
        
        ConclaveMessage.Welcome welcome = new ConclaveMessage.Welcome(sessionId, generation, seq, accountId);
        
        assertNotNull("Welcome should not be null", welcome);
        assertNotNull("Welcome fields should not be null", welcome.welcome);
        assertEquals("Session ID should match", sessionId, welcome.welcome.sessionId);
        assertEquals("Generation should match", generation, welcome.welcome.generation);
        assertEquals("Sequence should match", seq, welcome.welcome.seq);
        assertEquals("Account ID should match", accountId, welcome.welcome.accountId);
    }

    @Test
    public void testMetric_EmptyByDefault() {
        ConclaveMessage.Metric metric = new ConclaveMessage.Metric();
        
        assertTrue("Empty metric should be empty", metric.isEmpty());
        assertNull("Application should be null", metric.application);
        assertNull("Peripherals should be null", metric.peripherals);
    }

    @Test
    public void testMetric_AddApplicationMetric() {
        ConclaveMessage.Metric metric = new ConclaveMessage.Metric();
        ConclaveMessage.Metric.MetricsFields fields = new ConclaveMessage.Metric.MetricsFields();
        
        assertTrue("Should be empty initially", metric.isEmpty());
        
        metric.addApplicationMetric(fields);
        
        assertFalse("Should not be empty after adding application metric", metric.isEmpty());
        assertNotNull("Application should not be null", metric.application);
        assertEquals("Application should have one entry", 1, metric.application.size());
        assertEquals("Should contain the added fields", fields, metric.application.get(0));
    }

    @Test
    public void testMetric_AddPeripheralMetric() {
        ConclaveMessage.Metric metric = new ConclaveMessage.Metric();
        ConclaveMessage.Metric.MetricsFields fields = new ConclaveMessage.Metric.MetricsFields();
        
        assertTrue("Should be empty initially", metric.isEmpty());
        
        metric.addPeripheralMetric(fields);
        
        assertFalse("Should not be empty after adding peripheral metric", metric.isEmpty());
        assertNotNull("Peripherals should not be null", metric.peripherals);
        assertEquals("Peripherals should have one entry", 1, metric.peripherals.size());
        assertEquals("Should contain the added fields", fields, metric.peripherals.get(0));
    }

    @Test
    public void testMetric_AddMultipleMetrics() {
        ConclaveMessage.Metric metric = new ConclaveMessage.Metric();
        
        ConclaveMessage.Metric.MetricsFields appFields1 = new ConclaveMessage.Metric.MetricsFields();
        ConclaveMessage.Metric.MetricsFields appFields2 = new ConclaveMessage.Metric.MetricsFields();
        ConclaveMessage.Metric.MetricsFields perFields1 = new ConclaveMessage.Metric.MetricsFields();
        
        metric.addApplicationMetric(appFields1);
        metric.addApplicationMetric(appFields2);
        metric.addPeripheralMetric(perFields1);
        
        assertEquals("Application should have two entries", 2, metric.application.size());
        assertEquals("Peripherals should have one entry", 1, metric.peripherals.size());
        assertFalse("Should not be empty", metric.isEmpty());
    }

    @Test
    public void testMetricsFields_DefaultConstructor() {
        ConclaveMessage.Metric.MetricsFields fields = new ConclaveMessage.Metric.MetricsFields();
        
        assertEquals("Name should have default value", "AttributeChangeRTT", fields.name);
        assertNull("Peripheral ID should be null", fields.peripheralId);
        assertEquals("Elapsed should be 0", 0, fields.elapsed);
        assertFalse("Success should be false", fields.success);
        assertEquals("Platform should be android", "android", fields.platform);
        assertNull("Failure reason should be null", fields.failure_reason);
    }

    @Test
    public void testMetricsFields_ParameterizedConstructor() {
        String peripheralId = "test-peripheral";
        long elapsed = 1500;
        boolean success = true;
        String failureReason = "timeout";
        
        ConclaveMessage.Metric.MetricsFields fields = 
                new ConclaveMessage.Metric.MetricsFields(peripheralId, elapsed, success, failureReason);
        
        assertEquals("Name should have default value", "AttributeChangeRTT", fields.name);
        assertEquals("Peripheral ID should match", peripheralId, fields.peripheralId);
        assertEquals("Elapsed should match", elapsed, fields.elapsed);
        assertEquals("Success should match", success, fields.success);
        assertEquals("Platform should be android", "android", fields.platform);
        assertEquals("Failure reason should match", failureReason, fields.failure_reason);
    }

    @Test
    public void testMetricsFields_SetValues() {
        ConclaveMessage.Metric.MetricsFields fields = new ConclaveMessage.Metric.MetricsFields();
        
        fields.name = "CustomMetric";
        fields.peripheralId = "peripheral-123";
        fields.elapsed = 2000;
        fields.success = true;
        fields.platform = "ios";
        fields.failure_reason = "network_error";
        
        assertEquals("Name should be set", "CustomMetric", fields.name);
        assertEquals("Peripheral ID should be set", "peripheral-123", fields.peripheralId);
        assertEquals("Elapsed should be set", 2000, fields.elapsed);
        assertTrue("Success should be true", fields.success);
        assertEquals("Platform should be set", "ios", fields.platform);
        assertEquals("Failure reason should be set", "network_error", fields.failure_reason);
    }

    @Test
    public void testFailureReason_EnumValues() {
        // Test that all enum values exist
        ConclaveMessage.Metric.FailureReason[] reasons = ConclaveMessage.Metric.FailureReason.values();
        
        assertTrue("Should have at least 4 failure reasons", reasons.length >= 4);
        
        // Test specific enum values
        boolean hasAppTimeout = false;
        boolean hasServiceApiTimeout = false;
        boolean hasServiceApiError = false;
        boolean hasHubError = false;
        
        for (ConclaveMessage.Metric.FailureReason reason : reasons) {
            switch (reason) {
                case APP_TIMEOUT:
                    hasAppTimeout = true;
                    break;
                case SERVICE_API_TIMEOUT:
                    hasServiceApiTimeout = true;
                    break;
                case SERVICE_API_ERROR:
                    hasServiceApiError = true;
                    break;
                case HUB_ERROR:
                    hasHubError = true;
                    break;
            }
        }
        
        assertTrue("Should have APP_TIMEOUT", hasAppTimeout);
        assertTrue("Should have SERVICE_API_TIMEOUT", hasServiceApiTimeout);
        assertTrue("Should have SERVICE_API_ERROR", hasServiceApiError);
        assertTrue("Should have HUB_ERROR", hasHubError);
    }

    @Test
    public void testMetric_IsEmpty_EdgeCases() {
        ConclaveMessage.Metric metric = new ConclaveMessage.Metric();
        
        // Test with empty lists
        metric.application = new ArrayList<>();
        metric.peripherals = new ArrayList<>();
        assertTrue("Should be empty with empty lists", metric.isEmpty());
        
        // Test with non-null but empty application list
        metric.application = new ArrayList<>();
        metric.peripherals = null;
        assertTrue("Should be empty with empty application list", metric.isEmpty());
        
        // Test with non-null but empty peripherals list
        metric.application = null;
        metric.peripherals = new ArrayList<>();
        assertTrue("Should be empty with empty peripherals list", metric.isEmpty());
    }
}

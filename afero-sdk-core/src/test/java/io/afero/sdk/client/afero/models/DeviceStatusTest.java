/*
 * Copyright (c) 2014-2017 Afero, Inc. All rights reserved.
 */

package io.afero.sdk.client.afero.models;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for DeviceStatus model class.
 * Tests basic functionality, field assignments, and toString method.
 */
public class DeviceStatusTest {

    @Test
    public void testDeviceStatus_DefaultValues() {
        DeviceStatus status = new DeviceStatus();
        
        assertNull("Available should be null by default", status.available);
        assertNull("Visible should be null by default", status.visible);
        assertNull("Direct should be null by default", status.direct);
        assertNull("Connectable should be null by default", status.connectable);
        assertNull("Connected should be null by default", status.connected);
        assertNull("Linked should be null by default", status.linked);
        assertNull("RSSI should be null by default", status.rssi);
    }

    @Test
    public void testDeviceStatus_SetBooleanValues() {
        DeviceStatus status = new DeviceStatus();
        
        status.available = true;
        status.visible = false;
        status.direct = true;
        status.connectable = false;
        status.connected = true;
        status.linked = false;
        
        assertTrue("Available should be true", status.available);
        assertFalse("Visible should be false", status.visible);
        assertTrue("Direct should be true", status.direct);
        assertFalse("Connectable should be false", status.connectable);
        assertTrue("Connected should be true", status.connected);
        assertFalse("Linked should be false", status.linked);
    }

    @Test
    public void testDeviceStatus_SetRssiValue() {
        DeviceStatus status = new DeviceStatus();
        
        status.rssi = -45;
        
        assertEquals("RSSI should be set", Integer.valueOf(-45), status.rssi);
    }

    @Test
    public void testDeviceStatus_SetAllValues() {
        DeviceStatus status = new DeviceStatus();
        
        status.available = true;
        status.visible = true;
        status.direct = false;
        status.connectable = true;
        status.connected = false;
        status.linked = true;
        status.rssi = -60;
        
        assertTrue("Available should be true", status.available);
        assertTrue("Visible should be true", status.visible);
        assertFalse("Direct should be false", status.direct);
        assertTrue("Connectable should be true", status.connectable);
        assertFalse("Connected should be false", status.connected);
        assertTrue("Linked should be true", status.linked);
        assertEquals("RSSI should be -60", Integer.valueOf(-60), status.rssi);
    }

    @Test
    public void testDeviceStatus_ToString_WithNullValues() {
        DeviceStatus status = new DeviceStatus();
        
        String result = status.toString();
        
        assertNotNull("toString should not return null", result);
        assertTrue("Should contain available=null", result.contains("available=null"));
        assertTrue("Should contain visible=null", result.contains("visible=null"));
        assertTrue("Should contain direct=null", result.contains("direct=null"));
        assertTrue("Should contain connectable=null", result.contains("connectable=null"));
        assertTrue("Should contain connected=null", result.contains("connected=null"));
        assertTrue("Should contain linked=null", result.contains("linked=null"));
        assertTrue("Should contain rssi=null", result.contains("rssi=null"));
    }

    @Test
    public void testDeviceStatus_ToString_WithSetValues() {
        DeviceStatus status = new DeviceStatus();
        status.available = true;
        status.visible = false;
        status.direct = true;
        status.connectable = false;
        status.connected = true;
        status.linked = false;
        status.rssi = -75;
        
        String result = status.toString();
        
        assertNotNull("toString should not return null", result);
        assertTrue("Should contain available=true", result.contains("available=true"));
        assertTrue("Should contain visible=false", result.contains("visible=false"));
        assertTrue("Should contain direct=true", result.contains("direct=true"));
        assertTrue("Should contain connectable=false", result.contains("connectable=false"));
        assertTrue("Should contain connected=true", result.contains("connected=true"));
        assertTrue("Should contain linked=false", result.contains("linked=false"));
        assertTrue("Should contain rssi=-75", result.contains("rssi=-75"));
    }

    @Test
    public void testDeviceStatus_ToString_Structure() {
        DeviceStatus status = new DeviceStatus();
        status.available = true;
        status.rssi = -50;
        
        String result = status.toString();
        
        assertTrue("Should start with {", result.startsWith("{ "));
        assertTrue("Should end with }", result.endsWith(" }"));
        assertTrue("Should contain comma separators", result.contains(", "));
    }

    @Test
    public void testDeviceStatus_RssiEdgeValues() {
        DeviceStatus status = new DeviceStatus();
        
        // Test extreme RSSI values
        status.rssi = -120; // Very weak signal
        assertEquals("Should handle very weak signal", Integer.valueOf(-120), status.rssi);
        
        status.rssi = 0; // Theoretical maximum
        assertEquals("Should handle maximum signal", Integer.valueOf(0), status.rssi);
        
        status.rssi = 10; // Invalid but should be allowed
        assertEquals("Should handle invalid positive values", Integer.valueOf(10), status.rssi);
    }

    @Test
    public void testDeviceStatus_BooleanNullValues() {
        DeviceStatus status = new DeviceStatus();
        
        // Explicitly set to null
        status.available = null;
        status.visible = null;
        status.direct = null;
        status.connectable = null;
        status.connected = null;
        status.linked = null;
        
        assertNull("Available should remain null", status.available);
        assertNull("Visible should remain null", status.visible);
        assertNull("Direct should remain null", status.direct);
        assertNull("Connectable should remain null", status.connectable);
        assertNull("Connected should remain null", status.connected);
        assertNull("Linked should remain null", status.linked);
    }
}

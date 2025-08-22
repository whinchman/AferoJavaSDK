/*
 * Copyright (c) 2014-2017 Afero, Inc. All rights reserved.
 */

package io.afero.sdk.client.afero.models;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for DeviceTag model class.
 * Tests constants, field assignments, and basic functionality.
 */
public class DeviceTagTest {

    @Test
    public void testDeviceTag_Constants() {
        assertEquals("TAG_TYPE_ACCOUNT should be ACCOUNT", "ACCOUNT", DeviceTag.TAG_TYPE_ACCOUNT);
        assertEquals("TAG_TYPE_SYSTEM should be SYSTEM", "SYSTEM", DeviceTag.TAG_TYPE_SYSTEM);
    }

    @Test
    public void testDeviceTag_DefaultValues() {
        DeviceTag tag = new DeviceTag();
        
        assertNull("Device tag ID should be null by default", tag.deviceTagId);
        assertNull("Key should be null by default", tag.key);
        assertNull("Value should be null by default", tag.value);
        assertEquals("Device tag type should default to ACCOUNT", DeviceTag.TAG_TYPE_ACCOUNT, tag.deviceTagType);
        assertNull("Localization key should be null by default", tag.localizationKey);
    }

    @Test
    public void testDeviceTag_SetDeviceTagId() {
        DeviceTag tag = new DeviceTag();
        String tagId = "12345678-1234-1234-1234-123456789abc";
        
        tag.deviceTagId = tagId;
        
        assertEquals("Device tag ID should be set", tagId, tag.deviceTagId);
    }

    @Test
    public void testDeviceTag_SetKey() {
        DeviceTag tag = new DeviceTag();
        String key = "location";
        
        tag.key = key;
        
        assertEquals("Key should be set", key, tag.key);
    }

    @Test
    public void testDeviceTag_SetValue() {
        DeviceTag tag = new DeviceTag();
        String value = "living_room";
        
        tag.value = value;
        
        assertEquals("Value should be set", value, tag.value);
    }

    @Test
    public void testDeviceTag_SetDeviceTagType() {
        DeviceTag tag = new DeviceTag();
        
        // Test setting to SYSTEM type
        tag.deviceTagType = DeviceTag.TAG_TYPE_SYSTEM;
        assertEquals("Device tag type should be SYSTEM", DeviceTag.TAG_TYPE_SYSTEM, tag.deviceTagType);
        
        // Test setting back to ACCOUNT type
        tag.deviceTagType = DeviceTag.TAG_TYPE_ACCOUNT;
        assertEquals("Device tag type should be ACCOUNT", DeviceTag.TAG_TYPE_ACCOUNT, tag.deviceTagType);
    }

    @Test
    public void testDeviceTag_SetLocalizationKey() {
        DeviceTag tag = new DeviceTag();
        String locKey = "en_US";
        
        tag.localizationKey = locKey;
        
        assertEquals("Localization key should be set", locKey, tag.localizationKey);
    }

    @Test
    public void testDeviceTag_SetAllFields() {
        DeviceTag tag = new DeviceTag();
        
        String tagId = "abcdef12-3456-7890-abcd-ef1234567890";
        String key = "room";
        String value = "bedroom";
        String tagType = DeviceTag.TAG_TYPE_SYSTEM;
        String locKey = "fr_FR";
        
        tag.deviceTagId = tagId;
        tag.key = key;
        tag.value = value;
        tag.deviceTagType = tagType;
        tag.localizationKey = locKey;
        
        assertEquals("Device tag ID should be set", tagId, tag.deviceTagId);
        assertEquals("Key should be set", key, tag.key);
        assertEquals("Value should be set", value, tag.value);
        assertEquals("Device tag type should be set", tagType, tag.deviceTagType);
        assertEquals("Localization key should be set", locKey, tag.localizationKey);
    }

    @Test
    public void testDeviceTag_KeyValueUsage() {
        DeviceTag tag = new DeviceTag();
        
        // Test typical key-value usage
        tag.key = "device_name";
        tag.value = "Smart Thermostat";
        
        assertEquals("Key should be set for organizational use", "device_name", tag.key);
        assertEquals("Value should contain the actual tag data", "Smart Thermostat", tag.value);
    }

    @Test
    public void testDeviceTag_ValueOnlyUsage() {
        DeviceTag tag = new DeviceTag();
        
        // Test value-only usage (key is optional)
        tag.value = "critical_device";
        
        assertNull("Key can be null when using value-only tags", tag.key);
        assertEquals("Value should contain the tag data", "critical_device", tag.value);
    }

    @Test
    public void testDeviceTag_DelimitedValue() {
        DeviceTag tag = new DeviceTag();
        
        // Test using delimiter in value to create key/value pair
        tag.value = "priority:high";
        
        assertEquals("Value can contain delimited data", "priority:high", tag.value);
        assertTrue("Value should contain delimiter", tag.value.contains(":"));
        
        // Simulate parsing the delimited value
        String[] parts = tag.value.split(":");
        assertEquals("Should split into key part", "priority", parts[0]);
        assertEquals("Should split into value part", "high", parts[1]);
    }

    @Test
    public void testDeviceTag_EmptyStringValues() {
        DeviceTag tag = new DeviceTag();
        
        tag.deviceTagId = "";
        tag.key = "";
        tag.value = "";
        tag.localizationKey = "";
        
        assertEquals("Empty device tag ID should be allowed", "", tag.deviceTagId);
        assertEquals("Empty key should be allowed", "", tag.key);
        assertEquals("Empty value should be allowed", "", tag.value);
        assertEquals("Empty localization key should be allowed", "", tag.localizationKey);
    }

    @Test
    public void testDeviceTag_NullValues() {
        DeviceTag tag = new DeviceTag();
        
        // Explicitly set fields to null
        tag.deviceTagId = null;
        tag.key = null;
        tag.value = null;
        tag.deviceTagType = null;
        tag.localizationKey = null;
        
        assertNull("Device tag ID can be null", tag.deviceTagId);
        assertNull("Key can be null", tag.key);
        assertNull("Value can be null", tag.value);
        assertNull("Device tag type can be null", tag.deviceTagType);
        assertNull("Localization key can be null", tag.localizationKey);
    }

    @Test
    public void testDeviceTag_LongValues() {
        DeviceTag tag = new DeviceTag();
        
        // Test with long strings
        String longKey = "this_is_a_very_long_key_name_that_might_be_used_for_detailed_organization";
        String longValue = "this_is_a_very_long_value_that_contains_detailed_information_about_the_device_and_its_configuration";
        
        tag.key = longKey;
        tag.value = longValue;
        
        assertEquals("Long key should be handled", longKey, tag.key);
        assertEquals("Long value should be handled", longValue, tag.value);
    }

    @Test
    public void testDeviceTag_SpecialCharacters() {
        DeviceTag tag = new DeviceTag();
        
        // Test with special characters
        tag.key = "device-name_v2.1";
        tag.value = "Smart Device™ (Model #123)";
        
        assertEquals("Key with special characters should be handled", "device-name_v2.1", tag.key);
        assertEquals("Value with special characters should be handled", "Smart Device™ (Model #123)", tag.value);
    }
}

/*
 * Copyright (c) 2014-2017 Afero, Inc. All rights reserved.
 */

package io.afero.sdk.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.Before;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for JSONUtils functionality including serialization,
 * deserialization, and error handling with various data types.
 */
public class JSONUtilsTest {

    private TestObject testObject;
    private String validJsonString;

    public static class TestObject {
        @JsonProperty("id")
        public String id;
        
        @JsonProperty("name")
        public String name;
        
        @JsonProperty("value")
        public Integer value;
        
        @JsonProperty("active")
        public Boolean active;

        public TestObject() {}

        public TestObject(String id, String name, Integer value, Boolean active) {
            this.id = id;
            this.name = name;
            this.value = value;
            this.active = active;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestObject that = (TestObject) obj;
            return (id != null ? id.equals(that.id) : that.id == null) &&
                   (name != null ? name.equals(that.name) : that.name == null) &&
                   (value != null ? value.equals(that.value) : that.value == null) &&
                   (active != null ? active.equals(that.active) : that.active == null);
        }
    }

    @Before
    public void setUp() {
        testObject = new TestObject("test123", "Test Device", 42, true);
        validJsonString = "{\"id\":\"test123\",\"name\":\"Test Device\",\"value\":42,\"active\":true}";
    }

    @Test
    public void testObjectMapperNotNull() {
        assertNotNull("ObjectMapper should not be null", JSONUtils.getObjectMapper());
    }

    @Test
    public void testObjectMapperSingleton() {
        assertSame("ObjectMapper should be singleton", 
                   JSONUtils.getObjectMapper(), 
                   JSONUtils.getObjectMapper());
    }

    @Test
    public void testWriteValueAsString() throws JsonProcessingException {
        String json = JSONUtils.writeValueAsString(testObject);
        assertNotNull("JSON string should not be null", json);
        assertTrue("JSON should contain id", json.contains("\"id\":\"test123\""));
        assertTrue("JSON should contain name", json.contains("\"name\":\"Test Device\""));
        assertTrue("JSON should contain value", json.contains("\"value\":42"));
        assertTrue("JSON should contain active", json.contains("\"active\":true"));
    }

    @Test
    public void testWriteValueAsPrettyString() throws JsonProcessingException {
        String prettyJson = JSONUtils.writeValueAsPrettyString(testObject);
        assertNotNull("Pretty JSON string should not be null", prettyJson);
        assertTrue("Pretty JSON should contain newlines", prettyJson.contains("\n"));
        assertTrue("Pretty JSON should contain proper indentation", prettyJson.contains("  "));
    }

    @Test
    public void testReadValueFromString() throws IOException {
        TestObject deserializedObject = JSONUtils.readValue(validJsonString, TestObject.class);
        assertNotNull("Deserialized object should not be null", deserializedObject);
        assertEquals("Objects should be equal", testObject, deserializedObject);
    }

    @Test
    public void testReadValueFromBytes() throws IOException {
        byte[] jsonBytes = validJsonString.getBytes();
        TestObject deserializedObject = JSONUtils.readValue(jsonBytes, TestObject.class);
        assertNotNull("Deserialized object should not be null", deserializedObject);
        assertEquals("Objects should be equal", testObject, deserializedObject);
    }

    @Test
    public void testRoundTripSerialization() throws IOException {
        // Serialize to JSON
        String json = JSONUtils.writeValueAsString(testObject);
        
        // Deserialize back to object
        TestObject roundTripObject = JSONUtils.readValue(json, TestObject.class);
        
        // Should be equal to original
        assertEquals("Round trip should preserve object equality", testObject, roundTripObject);
    }

    @Test
    public void testNullValueSerialization() throws JsonProcessingException {
        TestObject nullValueObject = new TestObject("test", null, null, null);
        String json = JSONUtils.writeValueAsString(nullValueObject);
        
        // Should not contain null values (based on NON_NULL configuration)
        assertFalse("JSON should not contain null name", json.contains("\"name\":null"));
        assertFalse("JSON should not contain null value", json.contains("\"value\":null"));
        assertFalse("JSON should not contain null active", json.contains("\"active\":null"));
    }

    @Test
    public void testEmptyObjectSerialization() throws IOException {
        TestObject emptyObject = new TestObject();
        String json = JSONUtils.writeValueAsString(emptyObject);
        
        // Should serialize to empty object or minimal representation
        assertNotNull("JSON should not be null", json);
        
        // Deserialize back
        TestObject deserializedEmpty = JSONUtils.readValue(json, TestObject.class);
        assertNotNull("Deserialized empty object should not be null", deserializedEmpty);
    }

    @Test
    public void testMapSerialization() throws IOException {
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("string", "value");
        testMap.put("number", 123);
        testMap.put("boolean", false);
        
        String json = JSONUtils.writeValueAsString(testMap);
        assertNotNull("Map JSON should not be null", json);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> deserializedMap = JSONUtils.readValue(json, Map.class);
        assertNotNull("Deserialized map should not be null", deserializedMap);
        assertEquals("String value should match", "value", deserializedMap.get("string"));
        assertEquals("Number value should match", 123, deserializedMap.get("number"));
        assertEquals("Boolean value should match", false, deserializedMap.get("boolean"));
    }

    @Test(expected = IOException.class)
    public void testInvalidJsonString() throws IOException {
        String invalidJson = "{invalid json}";
        JSONUtils.readValue(invalidJson, TestObject.class);
    }

    @Test(expected = IOException.class)
    public void testInvalidJsonBytes() throws IOException {
        byte[] invalidJsonBytes = "{invalid json}".getBytes();
        JSONUtils.readValue(invalidJsonBytes, TestObject.class);
    }

    @Test
    public void testSpecialCharacters() throws IOException {
        TestObject specialObject = new TestObject("test\n\t\"special", "Special\u2603Characters", -1, false);
        String json = JSONUtils.writeValueAsString(specialObject);
        
        TestObject deserializedSpecial = JSONUtils.readValue(json, TestObject.class);
        assertEquals("Special characters should be preserved", specialObject, deserializedSpecial);
    }

    @Test
    public void testLargeNumbers() throws IOException {
        TestObject largeNumberObject = new TestObject("big", "Big Number", Integer.MAX_VALUE, true);
        String json = JSONUtils.writeValueAsString(largeNumberObject);
        
        TestObject deserializedLarge = JSONUtils.readValue(json, TestObject.class);
        assertEquals("Large numbers should be preserved", largeNumberObject, deserializedLarge);
    }
}

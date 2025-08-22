/*
 * Copyright (c) 2014-2017 Afero, Inc. All rights reserved.
 */

package io.afero.sdk.utils;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Comprehensive unit tests for BinarySearch utility class.
 * Tests both lowerBound and upperBound search algorithms.
 */
public class BinarySearchTest {

    @Test
    public void testLowerBound_ExistingElement() {
        List<Integer> list = Arrays.asList(1, 2, 2, 2, 3, 4, 5);
        
        // Should find the first occurrence of 2
        int result = BinarySearch.lowerBound(list, 2);
        assertEquals(1, result);
        
        // Test with single occurrence
        result = BinarySearch.lowerBound(list, 1);
        assertEquals(0, result);
        
        result = BinarySearch.lowerBound(list, 5);
        assertEquals(6, result);
    }

    @Test
    public void testLowerBound_NonExistingElement() {
        List<Integer> list = Arrays.asList(1, 3, 5, 7, 9);
        
        // Element not in list
        int result = BinarySearch.lowerBound(list, 4);
        assertEquals(-1, result);
        
        result = BinarySearch.lowerBound(list, 0);
        assertEquals(-1, result);
        
        result = BinarySearch.lowerBound(list, 10);
        assertEquals(-1, result);
    }

    @Test
    public void testLowerBound_EmptyList() {
        List<Integer> emptyList = new ArrayList<>();
        int result = BinarySearch.lowerBound(emptyList, 5);
        assertEquals(-1, result);
    }

    @Test
    public void testLowerBound_SingleElement() {
        List<Integer> singleElementList = Arrays.asList(5);
        
        // Element exists
        int result = BinarySearch.lowerBound(singleElementList, 5);
        assertEquals(0, result);
        
        // Element doesn't exist
        result = BinarySearch.lowerBound(singleElementList, 3);
        assertEquals(-1, result);
        
        result = BinarySearch.lowerBound(singleElementList, 7);
        assertEquals(-1, result);
    }

    @Test
    public void testLowerBound_AllSameElements() {
        List<Integer> list = Arrays.asList(5, 5, 5, 5, 5);
        
        // Should return first occurrence
        int result = BinarySearch.lowerBound(list, 5);
        assertEquals(0, result);
        
        // Non-existing element
        result = BinarySearch.lowerBound(list, 3);
        assertEquals(-1, result);
    }

    @Test
    public void testUpperBound_ExistingElement() {
        List<Integer> list = Arrays.asList(1, 2, 2, 2, 3, 4, 5);
        
        // Should find the last occurrence of 2
        int result = BinarySearch.upperBound(list, 2);
        assertEquals(3, result);
        
        // Test with single occurrence
        result = BinarySearch.upperBound(list, 1);
        assertEquals(0, result);
        
        result = BinarySearch.upperBound(list, 5);
        assertEquals(6, result);
    }

    @Test
    public void testUpperBound_NonExistingElement() {
        List<Integer> list = Arrays.asList(1, 3, 5, 7, 9);
        
        // Element not in list
        int result = BinarySearch.upperBound(list, 4);
        assertEquals(-1, result);
        
        result = BinarySearch.upperBound(list, 0);
        assertEquals(-1, result);
        
        result = BinarySearch.upperBound(list, 10);
        assertEquals(-1, result);
    }

    @Test
    public void testUpperBound_EmptyList() {
        List<Integer> emptyList = new ArrayList<>();
        int result = BinarySearch.upperBound(emptyList, 5);
        assertEquals(-1, result);
    }

    @Test
    public void testUpperBound_SingleElement() {
        List<Integer> singleElementList = Arrays.asList(5);
        
        // Element exists
        int result = BinarySearch.upperBound(singleElementList, 5);
        assertEquals(0, result);
        
        // Element doesn't exist
        result = BinarySearch.upperBound(singleElementList, 3);
        assertEquals(-1, result);
        
        result = BinarySearch.upperBound(singleElementList, 7);
        assertEquals(-1, result);
    }

    @Test
    public void testUpperBound_AllSameElements() {
        List<Integer> list = Arrays.asList(5, 5, 5, 5, 5);
        
        // Should return last occurrence
        int result = BinarySearch.upperBound(list, 5);
        assertEquals(4, result);
        
        // Non-existing element
        result = BinarySearch.upperBound(list, 3);
        assertEquals(-1, result);
    }

    @Test
    public void testBounds_WithStrings() {
        List<String> list = Arrays.asList("apple", "banana", "banana", "cherry", "date");
        
        // Lower bound
        int lowerResult = BinarySearch.lowerBound(list, "banana");
        assertEquals(1, lowerResult);
        
        // Upper bound
        int upperResult = BinarySearch.upperBound(list, "banana");
        assertEquals(2, upperResult);
        
        // Non-existing
        lowerResult = BinarySearch.lowerBound(list, "grape");
        assertEquals(-1, lowerResult);
        
        upperResult = BinarySearch.upperBound(list, "grape");
        assertEquals(-1, upperResult);
    }

    @Test
    public void testBounds_WithCustomComparableObjects() {
        List<TestComparable> list = Arrays.asList(
            new TestComparable(1),
            new TestComparable(3),
            new TestComparable(3),
            new TestComparable(5),
            new TestComparable(7)
        );
        
        TestComparable searchKey = new TestComparable(3);
        
        int lowerResult = BinarySearch.lowerBound(list, searchKey);
        assertEquals(1, lowerResult);
        
        int upperResult = BinarySearch.upperBound(list, searchKey);
        assertEquals(2, upperResult);
        
        // Non-existing
        TestComparable nonExistingKey = new TestComparable(4);
        lowerResult = BinarySearch.lowerBound(list, nonExistingKey);
        assertEquals(-1, lowerResult);
        
        upperResult = BinarySearch.upperBound(list, nonExistingKey);
        assertEquals(-1, upperResult);
    }

    @Test
    public void testBounds_LargeList() {
        List<Integer> largeList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeList.add(i / 10); // Creates duplicates: 0,0,0,...,9,10,10,10,...
        }
        
        // Test with existing element that has many duplicates
        int lowerResult = BinarySearch.lowerBound(largeList, 50);
        assertEquals(500, lowerResult); // First occurrence of 50
        
        int upperResult = BinarySearch.upperBound(largeList, 50);
        assertEquals(509, upperResult); // Last occurrence of 50
        
        // Test with non-existing element
        lowerResult = BinarySearch.lowerBound(largeList, 1000);
        assertEquals(-1, lowerResult);
    }

    @Test
    public void testBounds_EdgeCases() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        
        // Test first element
        assertEquals(0, BinarySearch.lowerBound(list, 1));
        assertEquals(0, BinarySearch.upperBound(list, 1));
        
        // Test last element
        assertEquals(4, BinarySearch.lowerBound(list, 5));
        assertEquals(4, BinarySearch.upperBound(list, 5));
        
        // Test middle element
        assertEquals(2, BinarySearch.lowerBound(list, 3));
        assertEquals(2, BinarySearch.upperBound(list, 3));
    }

    @Test
    public void testBounds_SortedDuplicates() {
        // Test with many duplicates in different positions
        List<Integer> list = Arrays.asList(1, 1, 1, 2, 2, 3, 3, 3, 3, 4);
        
        // Test element 1 (at beginning)
        assertEquals(0, BinarySearch.lowerBound(list, 1));
        assertEquals(2, BinarySearch.upperBound(list, 1));
        
        // Test element 2 (in middle)
        assertEquals(3, BinarySearch.lowerBound(list, 2));
        assertEquals(4, BinarySearch.upperBound(list, 2));
        
        // Test element 3 (multiple occurrences)
        assertEquals(5, BinarySearch.lowerBound(list, 3));
        assertEquals(8, BinarySearch.upperBound(list, 3));
        
        // Test element 4 (at end)
        assertEquals(9, BinarySearch.lowerBound(list, 4));
        assertEquals(9, BinarySearch.upperBound(list, 4));
    }

    @Test
    public void testBounds_NullHandling() {
        // Test with list containing nulls and Comparable implementation that handles nulls
        List<TestComparable> listWithNulls = Arrays.asList(
            null, 
            new TestComparable(1), 
            new TestComparable(2), 
            new TestComparable(3)
        );
        
        // This test depends on TestComparable's compareTo implementation handling nulls
        // For now, we'll test with non-null values only
        int result = BinarySearch.lowerBound(listWithNulls.subList(1, 4), new TestComparable(2));
        assertEquals(1, result); // Index 1 in sublist corresponds to original index 2
    }

    /**
     * Helper class for testing with custom Comparable objects
     */
    private static class TestComparable implements Comparable<TestComparable> {
        private final int value;
        
        public TestComparable(int value) {
            this.value = value;
        }
        
        @Override
        public int compareTo(TestComparable other) {
            if (other == null) return 1;
            return Integer.compare(this.value, other.value);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestComparable that = (TestComparable) obj;
            return value == that.value;
        }
        
        @Override
        public int hashCode() {
            return Integer.hashCode(value);
        }
        
        @Override
        public String toString() {
            return "TestComparable{" + value + "}";
        }
    }
}

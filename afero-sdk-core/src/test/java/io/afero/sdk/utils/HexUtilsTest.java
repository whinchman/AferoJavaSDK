/*
 * Copyright (c) 2014-2017 Afero, Inc. All rights reserved.
 */

package io.afero.sdk.utils;

import org.junit.Test;
import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Comprehensive unit tests for HexUtils utility class.
 * Tests hex encoding/decoding functionality and byte buffer operations.
 */
public class HexUtilsTest {

    @Test
    public void testParseHexBinary_ValidInput() {
        // Test basic hex parsing
        byte[] result = HexUtils.parseHexBinary("48656C6C6F");
        assertArrayEquals(new byte[]{0x48, 0x65, 0x6C, 0x6C, 0x6F}, result);

        // Test lowercase hex
        result = HexUtils.parseHexBinary("48656c6c6f");
        assertArrayEquals(new byte[]{0x48, 0x65, 0x6C, 0x6C, 0x6F}, result);

        // Test mixed case
        result = HexUtils.parseHexBinary("48656C6c6F");
        assertArrayEquals(new byte[]{0x48, 0x65, 0x6C, 0x6C, 0x6F}, result);

        // Test empty string
        result = HexUtils.parseHexBinary("");
        assertArrayEquals(new byte[0], result);

        // Test single byte
        result = HexUtils.parseHexBinary("FF");
        assertArrayEquals(new byte[]{(byte) 0xFF}, result);

        // Test zero values
        result = HexUtils.parseHexBinary("0000");
        assertArrayEquals(new byte[]{0x00, 0x00}, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseHexBinary_OddLength() {
        HexUtils.parseHexBinary("123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseHexBinary_InvalidCharacters() {
        HexUtils.parseHexBinary("1G23");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseHexBinary_InvalidCharactersLowercase() {
        HexUtils.parseHexBinary("1g23");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseHexBinary_SpecialCharacters() {
        HexUtils.parseHexBinary("12@3");
    }

    @Test
    public void testPrintHexBinary_BasicArray() {
        byte[] input = {0x48, 0x65, 0x6C, 0x6C, 0x6F};
        String result = HexUtils.printHexBinary(input);
        assertEquals("48656C6C6F", result);

        // Test empty array
        result = HexUtils.printHexBinary(new byte[0]);
        assertEquals("", result);

        // Test single byte
        result = HexUtils.printHexBinary(new byte[]{(byte) 0xFF});
        assertEquals("FF", result);

        // Test negative bytes
        result = HexUtils.printHexBinary(new byte[]{(byte) 0x80, (byte) 0xFF});
        assertEquals("80FF", result);

        // Test zero values
        result = HexUtils.printHexBinary(new byte[]{0x00, 0x00});
        assertEquals("0000", result);
    }

    @Test
    public void testPrintHexBinary_WithOffsetAndLength() {
        byte[] input = {0x11, 0x22, 0x33, 0x44, 0x55, 0x66};
        
        // Test normal range
        String result = HexUtils.printHexBinary(input, 1, 4);
        assertEquals("223344", result);

        // Test from beginning
        result = HexUtils.printHexBinary(input, 0, 3);
        assertEquals("112233", result);

        // Test single byte
        result = HexUtils.printHexBinary(input, 2, 3);
        assertEquals("33", result);

        // Test zero length
        result = HexUtils.printHexBinary(input, 0, 0);
        assertEquals("", result);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testPrintHexBinary_OffsetOutOfBounds() {
        byte[] input = {0x11, 0x22, 0x33};
        HexUtils.printHexBinary(input, 0, 5);
    }

    @Test
    public void testToBytes_ArrayBackedBuffer() {
        byte[] originalBytes = {0x11, 0x22, 0x33, 0x44};
        ByteBuffer bb = ByteBuffer.wrap(originalBytes);
        
        byte[] result = HexUtils.toBytes(bb);
        
        // Should return the same array reference for array-backed buffers
        assertSame(originalBytes, result);
    }

    @Test
    public void testToBytes_DirectBuffer() {
        ByteBuffer bb = ByteBuffer.allocateDirect(4);
        bb.put(new byte[]{0x11, 0x22, 0x33, 0x44});
        bb.flip();
        
        byte[] result = HexUtils.toBytes(bb);
        
        assertArrayEquals(new byte[]{0x11, 0x22, 0x33, 0x44}, result);
        // Position should be preserved
        assertEquals(0, bb.position());
    }

    @Test
    public void testToBytes_PartialBuffer() {
        ByteBuffer bb = ByteBuffer.allocate(6);
        bb.put(new byte[]{0x11, 0x22, 0x33, 0x44, 0x55, 0x66});
        bb.position(2);
        bb.limit(5);
        
        byte[] result = HexUtils.toBytes(bb);
        
        // For array-backed buffers, returns entire array
        assertArrayEquals(new byte[]{0x11, 0x22, 0x33, 0x44, 0x55, 0x66}, result);
    }

    @Test
    public void testHexDecode() {
        ByteBuffer result = HexUtils.hexDecode("48656C6C6F");
        
        assertEquals(ByteOrder.LITTLE_ENDIAN, result.order());
        assertEquals(5, result.remaining());
        
        byte[] bytes = new byte[result.remaining()];
        result.get(bytes);
        assertArrayEquals(new byte[]{0x48, 0x65, 0x6C, 0x6C, 0x6F}, bytes);
    }

    @Test
    public void testHexDecode_EmptyString() {
        ByteBuffer result = HexUtils.hexDecode("");
        assertEquals(0, result.remaining());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHexDecode_InvalidInput() {
        HexUtils.hexDecode("GG");
    }

    @Test
    public void testHexEncode_ArrayBackedBuffer() {
        byte[] bytes = {0x48, 0x65, 0x6C, 0x6C, 0x6F};
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        
        String result = HexUtils.hexEncode(bb);
        assertEquals("48656C6C6F", result);
    }

    @Test
    public void testHexEncode_DirectBuffer() {
        ByteBuffer bb = ByteBuffer.allocateDirect(5);
        bb.put(new byte[]{0x48, 0x65, 0x6C, 0x6C, 0x6F});
        bb.flip();
        
        String result = HexUtils.hexEncode(bb);
        assertEquals("48656C6C6F", result);
    }

    @Test
    public void testHexEncode_EmptyBuffer() {
        ByteBuffer bb = ByteBuffer.allocate(0);
        String result = HexUtils.hexEncode(bb);
        assertEquals("", result);
    }

    @Test
    public void testRoundTrip_ParseAndPrint() {
        String original = "DEADBEEF1234567890ABCDEF";
        byte[] parsed = HexUtils.parseHexBinary(original);
        String printed = HexUtils.printHexBinary(parsed);
        assertEquals(original, printed);
    }

    @Test
    public void testRoundTrip_DecodeAndEncode() {
        String original = "DEADBEEF1234567890ABCDEF";
        ByteBuffer decoded = HexUtils.hexDecode(original);
        String encoded = HexUtils.hexEncode(decoded);
        assertEquals(original, encoded);
    }

    @Test
    public void testHexToBin_AllValidCharacters() {
        // Test through reflection or by testing the overall functionality
        // Since hexToBin is private, we test it indirectly through parseHexBinary
        
        // Test all hex digits
        String allHexDigits = "0123456789ABCDEFabcdef";
        byte[] result = HexUtils.parseHexBinary(allHexDigits);
        
        // Expected bytes for the hex string above
        byte[] expected = {
            0x01, 0x23, 0x45, 0x67, (byte)0x89, (byte)0xAB, (byte)0xCD, (byte)0xEF,
            (byte)0xab, (byte)0xcd, (byte)0xef
        };
        
        assertArrayEquals(expected, result);
    }

    @Test
    public void testEdgeCases_MaxValues() {
        // Test maximum byte value
        byte[] maxBytes = {(byte) 0xFF, (byte) 0xFF};
        String hexString = HexUtils.printHexBinary(maxBytes);
        assertEquals("FFFF", hexString);
        
        byte[] parsed = HexUtils.parseHexBinary(hexString);
        assertArrayEquals(maxBytes, parsed);
    }

    @Test
    public void testEdgeCases_MinValues() {
        // Test minimum byte value (all zeros)
        byte[] minBytes = {0x00, 0x00, 0x00, 0x00};
        String hexString = HexUtils.printHexBinary(minBytes);
        assertEquals("00000000", hexString);
        
        byte[] parsed = HexUtils.parseHexBinary(hexString);
        assertArrayEquals(minBytes, parsed);
    }

    @Test
    public void testBufferPositionPreservation() {
        ByteBuffer bb = ByteBuffer.allocateDirect(10);
        bb.put(new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        bb.position(3);
        bb.limit(7);
        
        int originalPosition = bb.position();
        int originalLimit = bb.limit();
        
        HexUtils.toBytes(bb);
        
        // Position and limit should be preserved
        assertEquals(originalPosition, bb.position());
        assertEquals(originalLimit, bb.limit());
    }
}

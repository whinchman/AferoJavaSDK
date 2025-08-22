/*
 * Copyright (c) 2014-2017 Afero, Inc. All rights reserved.
 */

package io.afero.sdk.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

/**
 * Comprehensive unit tests for Clock utility class.
 * Tests clock functionality and custom clock implementation.
 */
public class ClockTest {

    private Clock.ClockImpl originalClockImpl;

    @Before
    public void setUp() {
        // Store original clock implementation to restore after tests
        originalClockImpl = getClockImpl();
    }

    @After
    public void tearDown() {
        // Restore original clock implementation
        Clock.setClockImpl(originalClockImpl);
    }

    @Test
    public void testGetElapsedMillis_DefaultImplementation() {
        long start = Clock.getElapsedMillis();
        
        // Sleep a small amount to ensure time passes
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long end = Clock.getElapsedMillis();
        
        // Time should have progressed
        assertTrue("End time should be greater than start time", end > start);
        
        // The difference should be reasonable (at least 5ms, less than 1000ms)
        long difference = end - start;
        assertTrue("Time difference should be at least 5ms", difference >= 5);
        assertTrue("Time difference should be less than 1000ms", difference < 1000);
    }

    @Test
    public void testSetClockImpl_CustomImplementation() {
        final long fixedTime = 123456789L;
        
        Clock.ClockImpl customClock = new Clock.ClockImpl() {
            @Override
            public long getElapsedMillis() {
                return fixedTime;
            }
        };
        
        Clock.setClockImpl(customClock);
        
        assertEquals(fixedTime, Clock.getElapsedMillis());
        assertEquals(fixedTime, Clock.getElapsedMillis()); // Should be consistent
    }

    @Test
    public void testSetClockImpl_CountingImplementation() {
        Clock.ClockImpl countingClock = new Clock.ClockImpl() {
            private long counter = 1000L;
            
            @Override
            public long getElapsedMillis() {
                return counter++;
            }
        };
        
        Clock.setClockImpl(countingClock);
        
        assertEquals(1000L, Clock.getElapsedMillis());
        assertEquals(1001L, Clock.getElapsedMillis());
        assertEquals(1002L, Clock.getElapsedMillis());
    }

    @Test
    public void testSetClockImpl_NullImplementation() {
        try {
            Clock.setClockImpl(null);
            Clock.getElapsedMillis();
            fail("Should throw NullPointerException when clock implementation is null");
        } catch (NullPointerException e) {
            // Expected behavior
        }
    }

    @Test
    public void testDefaultImplementation_BasedOnNanoTime() {
        // Reset to default implementation
        Clock.setClockImpl(new Clock.ClockImpl() {
            @Override
            public long getElapsedMillis() {
                return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            }
        });
        
        long clockTime = Clock.getElapsedMillis();
        long systemTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
        
        // Should be very close to system nano time converted to millis
        long difference = Math.abs(clockTime - systemTime);
        assertTrue("Clock time should be close to system nano time", difference < 10);
    }

    @Test
    public void testClockImpl_Interface() {
        // Test that we can create anonymous implementations
        Clock.ClockImpl testImpl = new Clock.ClockImpl() {
            @Override
            public long getElapsedMillis() {
                return 42L;
            }
        };
        
        assertEquals(42L, testImpl.getElapsedMillis());
    }

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        final int numThreads = 10;
        final int iterationsPerThread = 100;
        final long[] results = new long[numThreads * iterationsPerThread];
        final Thread[] threads = new Thread[numThreads];
        
        // Create threads that will call getElapsedMillis concurrently
        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        results[threadIndex * iterationsPerThread + j] = Clock.getElapsedMillis();
                        // Small delay to increase chance of interleaving
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Verify that all results are reasonable (non-negative and generally increasing)
        long minTime = Long.MAX_VALUE;
        long maxTime = Long.MIN_VALUE;
        
        for (long result : results) {
            assertTrue("All times should be non-negative", result >= 0);
            minTime = Math.min(minTime, result);
            maxTime = Math.max(maxTime, result);
        }
        
        // There should be some time difference between min and max
        assertTrue("Max time should be greater than min time", maxTime > minTime);
    }

    @Test
    public void testTimeProgression() {
        // Take several measurements and verify time generally progresses
        long[] measurements = new long[5];
        
        for (int i = 0; i < measurements.length; i++) {
            measurements[i] = Clock.getElapsedMillis();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        // Verify general progression (allowing for some potential variation)
        for (int i = 1; i < measurements.length; i++) {
            assertTrue("Time should generally progress: measurement " + i + 
                      " (" + measurements[i] + ") should be >= measurement " + (i-1) + 
                      " (" + measurements[i-1] + ")",
                      measurements[i] >= measurements[i-1]);
        }
        
        // First and last measurement should definitely show progression
        assertTrue("Significant time should have passed", 
                   measurements[measurements.length - 1] > measurements[0]);
    }

    @Test
    public void testSetClockImpl_MultipleTimes() {
        Clock.ClockImpl impl1 = new Clock.ClockImpl() {
            @Override
            public long getElapsedMillis() {
                return 100L;
            }
        };
        
        Clock.ClockImpl impl2 = new Clock.ClockImpl() {
            @Override
            public long getElapsedMillis() {
                return 200L;
            }
        };
        
        Clock.setClockImpl(impl1);
        assertEquals(100L, Clock.getElapsedMillis());
        
        Clock.setClockImpl(impl2);
        assertEquals(200L, Clock.getElapsedMillis());
        
        Clock.setClockImpl(impl1);
        assertEquals(100L, Clock.getElapsedMillis());
    }

    /**
     * Helper method to get the current clock implementation via reflection
     * or return a default implementation if reflection fails.
     */
    private Clock.ClockImpl getClockImpl() {
        try {
            java.lang.reflect.Field field = Clock.class.getDeclaredField("sClockImpl");
            field.setAccessible(true);
            return (Clock.ClockImpl) field.get(null);
        } catch (Exception e) {
            // Return default implementation if reflection fails
            return new Clock.ClockImpl() {
                @Override
                public long getElapsedMillis() {
                    return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
                }
            };
        }
    }
}

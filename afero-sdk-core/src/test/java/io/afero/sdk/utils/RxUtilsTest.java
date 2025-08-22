/*
 * Copyright (c) 2014-2017 Afero, Inc. All rights reserved.
 */

package io.afero.sdk.utils;

import org.junit.Test;
import static org.junit.Assert.*;

import rx.Observable;
import rx.Subscription;
import rx.observers.TestSubscriber;

/**
 * Comprehensive unit tests for RxUtils utility class.
 * Tests subscription management, reactive utilities, and helper classes.
 */
public class RxUtilsTest {

    @Test
    public void testSafeUnSubscribe_WithValidSubscription() {
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        // Use an Observable that doesn't complete immediately
        Observable<String> observable = Observable.never(); // This never completes
        Subscription subscription = observable.subscribe(subscriber);
        
        assertFalse("Subscription should be active", subscription.isUnsubscribed());
        
        Subscription result = RxUtils.safeUnSubscribe(subscription);
        
        assertNull("Should return null", result);
        assertTrue("Subscription should be unsubscribed", subscription.isUnsubscribed());
    }

    @Test
    public void testSafeUnSubscribe_WithNullSubscription() {
        Subscription result = RxUtils.safeUnSubscribe(null);
        
        assertNull("Should return null for null input", result);
        // Should not throw any exception
    }

    @Test
    public void testSafeUnSubscribe_WithAlreadyUnsubscribed() {
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        Observable<String> observable = Observable.just("test");
        Subscription subscription = observable.subscribe(subscriber);
        
        subscription.unsubscribe();
        assertTrue("Subscription should already be unsubscribed", subscription.isUnsubscribed());
        
        Subscription result = RxUtils.safeUnSubscribe(subscription);
        
        assertNull("Should return null", result);
        assertTrue("Subscription should remain unsubscribed", subscription.isUnsubscribed());
    }

    @Test
    public void testIgnoreResponseObserver_OnNext() {
        RxUtils.IgnoreResponseObserver<String> observer = new RxUtils.IgnoreResponseObserver<>();
        
        // Should not throw any exceptions
        observer.onNext("test");
        observer.onNext(null);
    }

    @Test
    public void testIgnoreResponseObserver_OnCompleted() {
        RxUtils.IgnoreResponseObserver<String> observer = new RxUtils.IgnoreResponseObserver<>();
        
        // Should not throw any exceptions
        observer.onCompleted();
    }

    @Test
    public void testIgnoreResponseObserver_OnError() {
        RxUtils.IgnoreResponseObserver<String> observer = new RxUtils.IgnoreResponseObserver<>();
        
        // Should not throw any exceptions, should log error internally
        observer.onError(new RuntimeException("Test error"));
        observer.onError(new IllegalArgumentException("Another test error"));
    }

    @Test
    public void testMapper_ReturnsExpectedObject() {
        String expectedResult = "mapped_result";
        RxUtils.Mapper<String, String> mapper = new RxUtils.Mapper<>(expectedResult);
        
        String result1 = mapper.call("input1");
        String result2 = mapper.call("input2");
        String result3 = mapper.call(null);
        
        assertEquals("Should return the expected object", expectedResult, result1);
        assertEquals("Should return the expected object for different input", expectedResult, result2);
        assertEquals("Should return the expected object for null input", expectedResult, result3);
    }

    @Test
    public void testMapper_WithNullObject() {
        RxUtils.Mapper<String, String> mapper = new RxUtils.Mapper<>(null);
        
        String result = mapper.call("input");
        
        assertNull("Should return null when initialized with null", result);
    }

    @Test
    public void testMapper_WithDifferentTypes() {
        Integer expectedResult = 42;
        RxUtils.Mapper<String, Integer> mapper = new RxUtils.Mapper<>(expectedResult);
        
        Integer result = mapper.call("any_string");
        
        assertEquals("Should return the expected integer", expectedResult, result);
    }

    @Test
    public void testFlatMapper_ReturnsExpectedObservable() {
        String expectedValue = "flattened_result";
        Observable<String> expectedObservable = Observable.just(expectedValue);
        
        RxUtils.FlatMapper<String, String> flatMapper = new RxUtils.FlatMapper<>(expectedObservable);
        
        Observable<String> result = flatMapper.call("input");
        
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        result.subscribe(subscriber);
        
        subscriber.assertValue(expectedValue);
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
    }

    @Test
    public void testReturnFunc0_ReturnsExpectedValue() {
        String expectedValue = "returned_value";
        RxUtils.ReturnFunc0<String> returnFunc = new RxUtils.ReturnFunc0<>(expectedValue);
        
        String result = returnFunc.call();
        
        assertEquals("Should return the expected value", expectedValue, result);
    }

    @Test
    public void testReturnFunc0_WithNullValue() {
        RxUtils.ReturnFunc0<String> returnFunc = new RxUtils.ReturnFunc0<>(null);
        
        String result = returnFunc.call();
        
        assertNull("Should return null when initialized with null", result);
    }

    @Test
    public void testReturnFunc0_WithDifferentTypes() {
        Boolean expectedValue = true;
        RxUtils.ReturnFunc0<Boolean> returnFunc = new RxUtils.ReturnFunc0<>(expectedValue);
        
        Boolean result = returnFunc.call();
        
        assertEquals("Should return the expected boolean", expectedValue, result);
    }

    @Test
    public void testIgnoreResponseObserver_AsRxObserver() {
        RxUtils.IgnoreResponseObserver<String> observer = new RxUtils.IgnoreResponseObserver<>();
        Observable<String> observable = Observable.just("test1", "test2");
        
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();
        
        // Use the ignore observer in a real RX chain
        observable.subscribe(observer);
        
        // Should complete without errors (tested by not throwing exceptions)
    }

    @Test
    public void testMapper_InRxChain() {
        String mappedValue = "constant_result";
        RxUtils.Mapper<String, String> mapper = new RxUtils.Mapper<>(mappedValue);
        
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        
        Observable.just("input1", "input2", "input3")
                .map(mapper)
                .subscribe(subscriber);
        
        subscriber.assertValues(mappedValue, mappedValue, mappedValue);
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
    }

    @Test
    public void testReturnFunc0_AsFunc0() {
        String returnValue = "constant_value";
        RxUtils.ReturnFunc0<String> returnFunc = new RxUtils.ReturnFunc0<>(returnValue);
        
        // Test that it can be used as a Func0
        String result = returnFunc.call();
        assertEquals("Should return the expected value", returnValue, result);
        
        // Test multiple calls return the same value
        String result2 = returnFunc.call();
        assertEquals("Should return the same value on subsequent calls", returnValue, result2);
    }

    @Test
    public void testFlatMapper_InRxChain() {
        String flattenedValue = "flattened_result";
        Observable<String> flattenedObservable = Observable.just(flattenedValue);
        RxUtils.FlatMapper<String, String> flatMapper = new RxUtils.FlatMapper<>(flattenedObservable);
        
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        
        Observable.just("input1", "input2")
                .flatMap(flatMapper)
                .subscribe(subscriber);
        
        // Each input should be mapped to the same observable, so we get duplicates
        subscriber.assertValues(flattenedValue, flattenedValue);
        subscriber.assertCompleted();
        subscriber.assertNoErrors();
    }
}

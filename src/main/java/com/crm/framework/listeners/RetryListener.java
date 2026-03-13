package com.crm.framework.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Retry listener: automatically retries failed tests up to MAX_RETRY times.
 * Register this in your testng XML as a listener or via @Listeners annotation.
 * Acts as both IRetryAnalyzer and IAnnotationTransformer (auto-applies retry to all tests).
 */
public class RetryListener implements IRetryAnalyzer, IAnnotationTransformer {

    private static final Logger log = LogManager.getLogger(RetryListener.class);
    private static final int MAX_RETRY = 2;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY) {
            retryCount++;
            log.warn("Retrying test '{}' | Attempt {}/{}",
                    result.getName(), retryCount, MAX_RETRY);
            return true;
        }
        retryCount = 0;
        return false;
    }

    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryListener.class);
    }
}

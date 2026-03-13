package com.crm.framework.exceptions;

/**
 * Thrown when an Excel file cannot be read or parsed during test data loading.
 * Wraps underlying IO/POI errors into a domain-specific unchecked exception
 * so callers don't have to declare checked exceptions.
 */
public class ExcelReaderException extends RuntimeException {

    public ExcelReaderException(String message) {
        super(message);
    }

    public ExcelReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}

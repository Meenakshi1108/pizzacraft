// File: src/service/ValidationException.java
package service;

/**
 * Exception thrown when a validation error occurs
 */
public class ValidationException extends Exception {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
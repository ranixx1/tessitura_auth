
package com.tessitura.auth.exception;

public class InvalidResetRequestException extends RuntimeException {
    public InvalidResetRequestException(String message) {
        super(message);
    }
}
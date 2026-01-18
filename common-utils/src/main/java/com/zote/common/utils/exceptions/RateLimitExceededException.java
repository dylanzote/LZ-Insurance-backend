package com.zote.common.utils.exceptions;

public class RateLimitExceededException extends RuntimeException{
    public RateLimitExceededException(String message) {
        super(message);
    }
}

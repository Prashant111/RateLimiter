package org.rate_limiter.exceptions;

public class InvalidRateParams extends RuntimeException {
    public InvalidRateParams(String errorMessage) {
        super(errorMessage);
    }
}

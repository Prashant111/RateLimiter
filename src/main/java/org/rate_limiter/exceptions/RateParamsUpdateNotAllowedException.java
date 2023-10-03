package org.rate_limiter.exceptions;

public class RateParamsUpdateNotAllowedException extends RuntimeException {
    public RateParamsUpdateNotAllowedException(String errorMessage) {
        super(errorMessage);
    }
}

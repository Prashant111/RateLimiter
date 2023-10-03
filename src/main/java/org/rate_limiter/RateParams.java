package org.rate_limiter;

public record RateParams(long capacity, RateRequest rateRequest) {
}

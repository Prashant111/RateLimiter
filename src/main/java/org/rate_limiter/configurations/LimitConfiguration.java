package org.rate_limiter.configurations;

public record LimitConfiguration(long capacity, long tokensAddedPerSecond) {
}

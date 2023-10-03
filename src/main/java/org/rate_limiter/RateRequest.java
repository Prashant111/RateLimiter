package org.rate_limiter;

import java.util.concurrent.TimeUnit;

public record RateRequest(TimeUnit timeUnit, long count) {
}

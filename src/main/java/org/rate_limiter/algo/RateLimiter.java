package org.rate_limiter.algo;

import org.rate_limiter.User;

public interface RateLimiter {
    boolean allowRequest(User user);
}

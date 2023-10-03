package org.rate_limiter.configurations;

import java.time.Duration;

public class CommonConfiguration {

    public static final double DEFAULT_TOKENS_PER_CREDIT = 1.0;
    public static final Duration EXTRA_CREDITS_VALIDITY_DURATION = Duration.ofDays(5);

    private CommonConfiguration() {
    }
}

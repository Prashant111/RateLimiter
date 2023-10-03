package org.rate_limiter.configurations;

import org.rate_limiter.RateParams;
import org.rate_limiter.RateRequest;
import org.rate_limiter.SubscriptionType;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.rate_limiter.SubscriptionType.*;

public class SubscriptionBasedConfiguration {

    private SubscriptionBasedConfiguration() {
    }

    public static Map<SubscriptionType, RateParams> getSubscriptionConfigMapping() {
        return Map.ofEntries(
                Map.entry(FREE_USER, new RateParams(10, new RateRequest(TimeUnit.SECONDS, 2))),
                Map.entry(BASIC_USER, new RateParams(15, new RateRequest(TimeUnit.SECONDS, 5))),
                Map.entry(PREMIUM_USER, new RateParams(100, new RateRequest(TimeUnit.SECONDS, 20))),
                Map.entry(BUSINESS_USER,
                          new RateParams(200, new RateRequest(TimeUnit.SECONDS, 100))
                         )
                            );
    }
}

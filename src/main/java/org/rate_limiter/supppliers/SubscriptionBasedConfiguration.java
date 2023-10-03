package org.rate_limiter.supppliers;

import org.rate_limiter.SubscriptionType;
import org.rate_limiter.configurations.LimitConfiguration;

import java.util.Map;

import static org.rate_limiter.SubscriptionType.*;

public class SubscriptionBasedConfiguration {

    private SubscriptionBasedConfiguration() {
    }

    public static Map<SubscriptionType, LimitConfiguration> getSubscriptionConfigMapping() {
        return Map.ofEntries(
                Map.entry(FREE_USER, new LimitConfiguration(10, 2)),
                Map.entry(BASIC_USER, new LimitConfiguration(15, 5)),
                Map.entry(PREMIUM_USER, new LimitConfiguration(100, 20)),
                Map.entry(BUSINESS_USER, new LimitConfiguration(200, 100))
                            );
    }
}

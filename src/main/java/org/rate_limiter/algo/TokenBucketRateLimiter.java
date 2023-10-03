package org.rate_limiter.algo;

import org.rate_limiter.SubscriptionType;
import org.rate_limiter.User;
import org.rate_limiter.RateParams;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.rate_limiter.configurations.SubscriptionBasedConfiguration.getSubscriptionConfigMapping;

public class TokenBucketRateLimiter implements RateLimiter {
    private final Map<SubscriptionType, RateParams> subscriptionTypeLimitConfigurationMap;
    private final ConcurrentMap<User, TokenBucket> userBuckets;

    public TokenBucketRateLimiter() {
        subscriptionTypeLimitConfigurationMap = getSubscriptionConfigMapping();
        this.userBuckets = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(User user) {
        TokenBucket bucket = userBuckets.computeIfAbsent(user,
                                                         k -> new TokenBucket(
                                                                 subscriptionTypeLimitConfigurationMap.get(
                                                                         user.getType()))
                                                        );
        bucket.setExtraTokens(user.getPaidAdditionalRequests());
        return bucket.allowRequest();
    }

}

package org.rate_limiter.algo;

import org.rate_limiter.RateParams;
import org.rate_limiter.SubscriptionType;
import org.rate_limiter.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.rate_limiter.configurations.SubscriptionConfiguration.getSubscriptionTypeRateParamsMap;


public class TokenBucketRateLimiter implements RateLimiter {
    private final Map<SubscriptionType, RateParams> subscriptionTypeLimitConfigurationMap;
    private final ConcurrentMap<User, TokenBucket> userBuckets;

    public TokenBucketRateLimiter() {
        subscriptionTypeLimitConfigurationMap = getSubscriptionTypeRateParamsMap();
        this.userBuckets = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowRequest(User user) {
        TokenBucket bucket = userBuckets.computeIfAbsent(user,
                                                         k -> new TokenBucket(
                                                                 subscriptionTypeLimitConfigurationMap.get(
                                                                         user.getType()), user.getPaidCredits())
                                                        );
        return bucket.allowRequest();
    }

}

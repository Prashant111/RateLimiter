package org.rate_limiter.configurations;

import org.jetbrains.annotations.NotNull;
import org.rate_limiter.RateParams;
import org.rate_limiter.RateRequest;
import org.rate_limiter.SubscriptionType;
import org.rate_limiter.exceptions.InvalidRateParams;
import org.rate_limiter.exceptions.RateParamsUpdateNotAllowedException;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.rate_limiter.SubscriptionType.*;

public class SubscriptionConfiguration {
    private SubscriptionConfiguration() {

    }
    public static final String RATE_PARAMS_FIELDS_ARE_INVALID = "Rate params fields are invalid";
    public static final String NOT_ALLOWED = "Not allowed";
    private static final Map<SubscriptionType, RateParams> subscriptionTypeRateParamsMap;

    static {
        subscriptionTypeRateParamsMap = new EnumMap<>(
                Map.ofEntries(Map.entry(FREE_USER, new RateParams(10, new RateRequest(TimeUnit.SECONDS, 2))),
                              Map.entry(BASIC_USER, new RateParams(15, new RateRequest(TimeUnit.SECONDS, 5))),
                              Map.entry(PREMIUM_USER, new RateParams(100, new RateRequest(TimeUnit.SECONDS, 20))),
                              Map.entry(BUSINESS_USER, new RateParams(200, new RateRequest(TimeUnit.SECONDS, 100))),
                              Map.entry(CUSTOM_USER, new RateParams(10, new RateRequest(TimeUnit.SECONDS, 2)))
                             ));
    }

    public static Map<SubscriptionType, RateParams> getSubscriptionTypeRateParamsMap() {
        return subscriptionTypeRateParamsMap;
    }

    public static void updateSubscriptionConfiguration(@NotNull final SubscriptionType subscriptionType,
                                                       @NotNull final RateParams rateParams) {
        if (isInvalidOrContainsInvalidFields(rateParams)) throw new InvalidRateParams(RATE_PARAMS_FIELDS_ARE_INVALID);

        if (subscriptionType != CUSTOM_USER) throw new RateParamsUpdateNotAllowedException(NOT_ALLOWED);

        subscriptionTypeRateParamsMap.put(subscriptionType, rateParams);
    }

    private static boolean isInvalidOrContainsInvalidFields(@NotNull RateParams rateParams) {
        return Objects.isNull(rateParams.rateRequest())
                || Objects.isNull(rateParams.rateRequest().timeUnit())
                || rateParams.capacity() <= 0
                || rateParams.rateRequest().count() <= 0;
    }
}

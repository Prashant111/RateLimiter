package org.rate_limiter.algo;

import org.jetbrains.annotations.NotNull;
import org.rate_limiter.PaidCredit;
import org.rate_limiter.RateParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import static org.rate_limiter.configurations.CommonConfiguration.DEFAULT_TOKENS_PER_CREDIT;
import static org.rate_limiter.configurations.CommonConfiguration.EXTRA_CREDITS_VALIDITY_DURATION;

public class TokenBucket {
    private final RateParams rateParams;
    private final AtomicLong tokens;
    private final List<PaidCredit> paidCredits;
    private final AtomicLong usedTokens;
    private long lastRefillTime;


    public TokenBucket(RateParams rateParams, List<PaidCredit> paidCredits) {
        this.rateParams = rateParams;
        this.tokens = new AtomicLong(initiallyAllowedRequest(rateParams));
        this.usedTokens = new AtomicLong(0L);
        this.paidCredits = paidCredits;
        this.lastRefillTime = System.nanoTime();
    }

    private static long initiallyAllowedRequest(RateParams rateParams) {
        return Math.min(rateParams.capacity(), rateParams.rateRequest()
                                                         .count());
    }

    public boolean allowRequest() {
        refillTokens();
        return isRequestAllowed();
    }

    private void refillTokens() {
        long tokensToAdd = getTokenReplenishmentCount();
        if (tokensToAdd > 0) {
            updateTokens(tokensToAdd);
        }
    }

    private long getTokenReplenishmentCount() {
        long now = System.nanoTime();
        long timeElapsed = now - lastRefillTime;
        long fillRate = rateParams.rateRequest()
                                  .count();
        return timeElapsed * fillRate / rateParams.rateRequest()
                                                  .timeUnit()
                                                  .toNanos(1);
    }

    private void updateTokens(long tokensToAdd) {
        long capacity = rateParams.capacity();
        tokens.set(getMinimumTokensCount(capacity, tokensToAdd));
        lastRefillTime = System.nanoTime();
    }

    private long getMinimumTokensCount(long capacity, long tokensToAdd) {
        return Math.max(0, Math.min(capacity, tokens.get() + tokensToAdd));
    }

    private boolean isRequestAllowed() {
        return decrementTokenCountIfPositiveThenReturnIfTokenExists() || useUserCreditsIfExist();
    }

    private boolean decrementTokenCountIfPositiveThenReturnIfTokenExists() {
        return (tokens.longValue() > 0 && tokens.getAndDecrement() > 0);
    }

    private boolean useUserCreditsIfExist() {
        long validTokens = getValidTokensFromTokens();
        long eligibleTokens = validTokens - usedTokens.get();
        if (eligibleTokens > 0) {
            usedTokens.incrementAndGet();
            return true;
        }
        return false;
    }

    private long getValidTokensFromTokens() {
        long validCredits = this.paidCredits.stream()
                                            .filter(notExpiredCredits())
                                            .map(PaidCredit::credits)
                                            .map(AtomicLong::get)
                                            .mapToLong(Long::longValue)
                                            .sum();
        return getTokensCountFromCredits(validCredits);
    }

    @NotNull
    private static Predicate<PaidCredit> notExpiredCredits() {
        return paidCredit -> paidCredit.allocationTime()
                                       .plus(EXTRA_CREDITS_VALIDITY_DURATION)
                                       .isAfter(LocalDateTime.now());
    }

    private static long getTokensCountFromCredits(long validCredits) {
        return (long) (validCredits * DEFAULT_TOKENS_PER_CREDIT);
    }

}

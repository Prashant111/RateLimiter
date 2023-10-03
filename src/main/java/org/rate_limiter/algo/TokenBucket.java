package org.rate_limiter.algo;

import org.rate_limiter.RateParams;

import java.util.concurrent.atomic.AtomicLong;

public class TokenBucket {
    private final RateParams limitConfiguration;
    private final AtomicLong tokens;
    private AtomicLong extraTokens;
    private long lastRefillTime; // Timestamp of the last token replenishment


    public TokenBucket(RateParams limitConfiguration) {
        this.limitConfiguration = limitConfiguration;
        this.tokens = new AtomicLong(limitConfiguration.rateRequest()
                                                       .count());
        this.extraTokens = new AtomicLong(0L);
        this.lastRefillTime = System.nanoTime();
    }


    public boolean allowRequest() {
        refillTokens();
        return isRequestAllowed();
    }

    private boolean isRequestAllowed() {
        return decrementCounterIfPositiveThenReturnIfTokenExists() || useExtraRequestIfExists();
    }

    private boolean decrementCounterIfPositiveThenReturnIfTokenExists() {
        return (tokens.longValue() > 0 && tokens.getAndDecrement() > 0);
    }

    private boolean useExtraRequestIfExists() {
        if (extraTokens.get() > 0) {
            extraTokens.decrementAndGet();
            return true;
        }
        return false;
    }

    private void refillTokens() {
        long now = System.nanoTime();
        long timeElapsed = now - lastRefillTime;
        long fillRate = limitConfiguration.rateRequest()
                                          .count();
        long tokensToAdd = timeElapsed * fillRate / limitConfiguration.rateRequest()
                                                                      .timeUnit()
                                                                      .toNanos(1);

        if (tokensToAdd > 0) {
            long capacity = limitConfiguration.capacity();
            tokens.set(tokensCount(capacity, tokensToAdd));
            lastRefillTime = now;
        }
    }

    private long tokensCount(long capacity, long tokensToAdd) {
        return Math.max(0, Math.min(capacity, tokens.get() + tokensToAdd));
    }

    public void setExtraTokens(AtomicLong extraTokens) {
        this.extraTokens = extraTokens;
    }
}

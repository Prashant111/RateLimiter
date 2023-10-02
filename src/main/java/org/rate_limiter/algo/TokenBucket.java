package org.rate_limiter.algo;

import org.rate_limiter.configurations.LimitConfiguration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class TokenBucket {
    private final LimitConfiguration limitConfiguration;
    private final AtomicLong tokens;
    private AtomicLong extraTokens;
    private long lastRefillTime; // Timestamp of the last token replenishment


    public TokenBucket(LimitConfiguration limitConfiguration) {
        this.limitConfiguration = limitConfiguration;
        this.tokens = new AtomicLong(limitConfiguration.tokensAddedPerSecond());
        this.extraTokens = new AtomicLong(0L);
        this.lastRefillTime = System.nanoTime();
    }


    public boolean allowRequest() {
        refillTokens();
        return isRequestAllowed();
    }

    private boolean isRequestAllowed() {
        return decrementCounterIfPositiveThenReturnIfTokenExists() || useExtraCreditsIfExist();
    }

    private boolean decrementCounterIfPositiveThenReturnIfTokenExists() {
        return (tokens.longValue() >= 1 && tokens.getAndDecrement() >= 1);
    }

    private boolean useExtraCreditsIfExist() {
        if (extraTokens.get() >= 1) {
            extraTokens.decrementAndGet();
            return true;
        }
        return false;
    }

    private void refillTokens() {
        long now = System.nanoTime();
        long timeElapsed = now - lastRefillTime;
        long fillRate = limitConfiguration.tokensAddedPerSecond();
        long tokensToAdd = timeElapsed * fillRate / TimeUnit.SECONDS.toNanos(1);

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

package org.rate_limiter;

import java.util.concurrent.atomic.AtomicLong;

import static org.rate_limiter.supppliers.CreditsRequestsConversionConfiguration.DEFAULT_REQUESTS_PER_CREDIT;

public class User {

    private final long id;
    private final SubscriptionType type;

    private final AtomicLong credits;
    private final AtomicLong requestsCredited;

    public User(long id, SubscriptionType type) {
        this.id = id;
        this.type = type;
        this.credits = new AtomicLong(0L);
        this.requestsCredited = new AtomicLong(0L);
    }

    public void addCredits(long extraCredits) {
        this.credits.getAndAdd(extraCredits);
        this.requestsCredited.set((long) (this.credits.get() * DEFAULT_REQUESTS_PER_CREDIT));
    }

    public SubscriptionType getType() {
        return type;
    }

    public AtomicLong getCredits() {
        return credits;
    }

    public AtomicLong getRequestsCredited() {
        return requestsCredited;
    }
}

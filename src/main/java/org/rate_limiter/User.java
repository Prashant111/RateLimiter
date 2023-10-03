package org.rate_limiter;

import java.util.concurrent.atomic.AtomicLong;

import static org.rate_limiter.configurations.CreditsRequestsConversionConfiguration.DEFAULT_REQUESTS_PER_CREDIT;

public class User {

    private final long id;
    private final SubscriptionType type;

    private final AtomicLong paidCredits;
    private final AtomicLong paidAdditionalRequests;

    public User(long id, SubscriptionType type) {
        this.id = id;
        this.type = type;
        this.paidCredits = new AtomicLong(0L);
        this.paidAdditionalRequests = new AtomicLong(0L);
    }

    public void addCredits(long extraCredits) {
        this.paidCredits.getAndAdd(extraCredits);
        this.paidAdditionalRequests.set((long) (this.paidCredits.get() * DEFAULT_REQUESTS_PER_CREDIT));
    }

    public SubscriptionType getType() {
        return type;
    }

    public AtomicLong getPaidCredits() {
        return paidCredits;
    }

    public AtomicLong getPaidAdditionalRequests() {
        return paidAdditionalRequests;
    }
}

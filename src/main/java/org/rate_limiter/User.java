package org.rate_limiter;

import java.util.concurrent.atomic.AtomicLong;

public class User {

    private final int id;
    private final SubscriptionType type;

    private final AtomicLong credits;

    public User(int id, SubscriptionType type) {
        this.id = id;
        this.type = type;
        this.credits = new AtomicLong(0L);
    }

    public void addCredits(long extraCredits) {
        this.credits.addAndGet(extraCredits);
    }

    public SubscriptionType getType() {
        return type;
    }

    public AtomicLong getCredits() {
        return credits;
    }
}

package org.rate_limiter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class User {

    private final long id;
    private final SubscriptionType type;

    private final List<PaidCredit> paidCredits;

    public User(long id, SubscriptionType type) {
        this.id = id;
        this.type = type;
        this.paidCredits = Collections.synchronizedList(new ArrayList<>());
    }

    public void addCredits(long extraCredits) {
        this.paidCredits.add(new PaidCredit(new AtomicLong(extraCredits), LocalDateTime.now()));
    }

    public SubscriptionType getType() {
        return type;
    }

    public List<PaidCredit> getPaidCredits() {
        return paidCredits;
    }
}

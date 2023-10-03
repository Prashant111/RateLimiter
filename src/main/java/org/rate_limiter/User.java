package org.rate_limiter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.rate_limiter.configurations.CommonConfiguration.DEFAULT_TOKENS_PER_CREDIT;
import static org.rate_limiter.configurations.CommonConfiguration.EXTRA_CREDITS_VALIDITY_DURATION;

public class User {

    private final long id;
    private final SubscriptionType type;

    private final List<PaidCredit> paidCredits;
    private final AtomicLong paidAdditionalRequests;

    public User(long id, SubscriptionType type) {
        this.id = id;
        this.type = type;
        this.paidCredits = Collections.synchronizedList(new ArrayList<>());
        this.paidAdditionalRequests = new AtomicLong(0L);
    }

    public void addCredits(long extraCredits) {
        this.paidCredits.add(new PaidCredit(new AtomicLong(extraCredits), LocalDateTime.now()));
        updatePaidAdditionRequests();
    }

    private void updatePaidAdditionRequests() {
        long validCredits = this.paidCredits.stream()
                                            .filter(paidCredit -> paidCredit.allocationTime()
                                                                            .plus(EXTRA_CREDITS_VALIDITY_DURATION)
                                                                            .isAfter(LocalDateTime.now()))
                                            .map(PaidCredit::credits)
                                            .map(AtomicLong::get)
                                            .mapToLong(Long::longValue)
                                            .sum();
        long validRequestCount = (long) (validCredits * DEFAULT_TOKENS_PER_CREDIT);
        System.out.println("validRequestCount = " + validRequestCount);
        this.paidAdditionalRequests.set(validRequestCount);
    }

    public SubscriptionType getType() {
        return type;
    }

    public List<PaidCredit> getPaidCredits() {
        return paidCredits;
    }

    public AtomicLong getPaidAdditionalRequests() {
        return paidAdditionalRequests;
    }
}

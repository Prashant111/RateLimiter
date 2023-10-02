package org.rate_limiter.algo;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rate_limiter.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.rate_limiter.SubscriptionType.FREE_USER;

class TokenBucketRateLimiterTest {
    private RateLimiter rateLimiter;
    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        rateLimiter = new TokenBucketRateLimiter();
        user1 = new User(1, FREE_USER);
        user2 = new User(2, FREE_USER);
    }

    @Test
    void testRequestAllowed() {
        assertTrue(rateLimiter.allowRequest(user1));
        assertTrue(rateLimiter.allowRequest(user2));
    }

    @Test
    void testRequestDeniedDueToRateLimiting() throws InterruptedException {
        assertTrue(rateLimiter.allowRequest(user1));
        assertTrue(rateLimiter.allowRequest(user1));

        assertFalse(rateLimiter.allowRequest(user1));

        // Wait for token replenishment (2 seconds) and then attempt a request
        Thread.sleep(2000);

        assertTrue(rateLimiter.allowRequest(user1)); // Request allowed after waiting
    }

    @Test
    void testRequestDeniedBeforeGivingItASingleCreditDueToRateLimiting() throws InterruptedException {
        assertTrue(rateLimiter.allowRequest(user1));
        assertTrue(rateLimiter.allowRequest(user1));

        user1.addCredits(1);
        assertTrue(rateLimiter.allowRequest(user1));

        assertFalse(rateLimiter.allowRequest(user1));
    }

    @Test
    void testUserSpecificRateLimiting() {
        assertTrue(rateLimiter.allowRequest(user1));
        assertTrue(rateLimiter.allowRequest(user1));

        assertTrue(rateLimiter.allowRequest(user2));
    }
}
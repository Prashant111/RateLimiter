package org.rate_limiter.algo;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rate_limiter.User;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.rate_limiter.SubscriptionType.BASIC_USER;
import static org.rate_limiter.SubscriptionType.FREE_USER;

class TokenBucketRateLimiterTest {
    private RateLimiter rateLimiter;
    private User freeUser1;
    private User freeUser2;
    private User basicUser1;

    @BeforeEach
    public void setUp() {
        rateLimiter = new TokenBucketRateLimiter();
        freeUser1 = new User(1, FREE_USER);
        freeUser2 = new User(2, FREE_USER);
        basicUser1 = new User(3, BASIC_USER);
    }

    @Test
    void testRequestAllowed() {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser2));
    }

    @Test
    void testRequestRateLimitingReached() {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
    }

    @Test
    void testRequestRateLimitingReachedForDifferentUsers() {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));

        assertTrue(rateLimiter.allowRequest(freeUser2));
        assertTrue(rateLimiter.allowRequest(freeUser2));
    }

    @Test
    void testUserUnevenButWithinRateLimiting() {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));

        assertTrue(rateLimiter.allowRequest(freeUser2));
    }

    @Test
    void testRequestRateLimitingExceededForOneOfTheUser() {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertFalse(rateLimiter.allowRequest(freeUser1));

        assertTrue(rateLimiter.allowRequest(freeUser2));
        assertTrue(rateLimiter.allowRequest(freeUser2));
    }

    @Test
    void testRequestRateLimitingExceeded() {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertFalse(rateLimiter.allowRequest(freeUser1));
    }

    @Test
    void testRequestRateLimitingExceededButSavedByExtraCredit() {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        freeUser1.addCredits(1L);
        assertTrue(rateLimiter.allowRequest(freeUser1));
    }

    @Test
    void testRequestRateLimitingExceededButSavedByExtraCreditForTwoUsers() {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        freeUser1.addCredits(1L);
        assertTrue(rateLimiter.allowRequest(freeUser1));

        assertTrue(rateLimiter.allowRequest(freeUser2));
        assertTrue(rateLimiter.allowRequest(freeUser2));
        freeUser2.addCredits(1L);
        assertTrue(rateLimiter.allowRequest(freeUser2));
    }

    @Test
    void testRequestRateLimitingExceededButSavedByExtraCreditForOneUserButOtherUserExceededWithoutExtraCredit() {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        freeUser1.addCredits(1L);
        assertTrue(rateLimiter.allowRequest(freeUser1));

        assertTrue(rateLimiter.allowRequest(freeUser2));
        assertTrue(rateLimiter.allowRequest(freeUser2));
        assertFalse(rateLimiter.allowRequest(freeUser2));
    }

    @Test
    void testRequestDeniedDueToRateLimiting() throws InterruptedException {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertFalse(rateLimiter.allowRequest(freeUser1));

        // Wait for token replenishment (1 second) and then attempt a request
        TimeUnit.SECONDS.sleep(1);

        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertFalse(rateLimiter.allowRequest(freeUser1));
    }

    @Test
    void testRequestDeniedBeforeGivingItASingleCreditDueToRateLimiting() {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));

        freeUser1.addCredits(1);
        assertTrue(rateLimiter.allowRequest(freeUser1));

        assertFalse(rateLimiter.allowRequest(freeUser1));
    }

    @Test
    void testBasicUserRateLimiting() {
        assertTrue(rateLimiter.allowRequest(basicUser1));
        assertTrue(rateLimiter.allowRequest(basicUser1));
        assertTrue(rateLimiter.allowRequest(basicUser1));
        assertTrue(rateLimiter.allowRequest(basicUser1));
        assertTrue(rateLimiter.allowRequest(basicUser1));

        assertFalse(rateLimiter.allowRequest(basicUser1));
    }

}
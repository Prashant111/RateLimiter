package org.rate_limiter.algo;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rate_limiter.RateParams;
import org.rate_limiter.RateRequest;
import org.rate_limiter.SubscriptionType;
import org.rate_limiter.User;
import org.rate_limiter.exceptions.InvalidRateParams;
import org.rate_limiter.exceptions.RateParamsUpdateNotAllowedException;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.rate_limiter.SubscriptionType.*;
import static org.rate_limiter.configurations.SubscriptionConfiguration.getSubscriptionTypeRateParamsMap;
import static org.rate_limiter.configurations.SubscriptionConfiguration.updateSubscriptionConfiguration;

class TokenBucketRateLimiterTest {
    private RateLimiter rateLimiter;
    private User freeUser1;
    private User freeUser2;
    private User basicUser;
    private User premiumUser;
    private User businessUser;

    @BeforeEach
    public void setUp() {
        rateLimiter = new TokenBucketRateLimiter();
        freeUser1 = new User(1, FREE_USER);
        freeUser2 = new User(2, FREE_USER);
        basicUser = new User(3, BASIC_USER);
        premiumUser = new User(4, PREMIUM_USER);
        businessUser = new User(5, BUSINESS_USER);
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
        testUserTypeRateLimiting(FREE_USER, freeUser1);
    }

    private void testUserTypeRateLimiting(SubscriptionType subscriptionType, User user) {
        boolean allRequestAllowed = IntStream.iterate(0, i -> i + 1)
                                             .limit(getSubscriptionTypeRateParamsMap()
                                                            .get(subscriptionType)
                                                            .rateRequest()
                                                            .count())
                                             .boxed()
                                             .allMatch(index -> rateLimiter.allowRequest(user));
        assertTrue(allRequestAllowed);
        assertFalse(rateLimiter.allowRequest(user));
    }

    @Test
    void testRequestRateLimitingExceededButSavedByExtraCredit() {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        freeUser1.addCredits(1L);

        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertFalse(rateLimiter.allowRequest(freeUser1));
        assertFalse(rateLimiter.allowRequest(freeUser1));
    }

    @Test
    void testRequestRateLimitingExceededButSavedByExtraCredits() {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        freeUser1.addCredits(2L);

        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertFalse(rateLimiter.allowRequest(freeUser1));
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
    void testWaitAndTestIfTokenIncreasingOnNotUsing() throws InterruptedException {
        assertTrue(rateLimiter.allowRequest(freeUser1));
        TimeUnit.SECONDS.sleep(2);
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));

        assertFalse(rateLimiter.allowRequest(freeUser1));
    }

    @Test
    void testTimeDoesNotCountUntilFirstRequestIsReceivedOfUser() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        // No replenishment until the first request is received, so even after one waiting time tokens will be 2 only

        testUserTypeRateLimiting(FREE_USER, freeUser1);
    }

    @Test
    void testReplenishmentOfRequestMoreThanThanRefillRateButBelowCapacity() throws InterruptedException {
        //Initially 2

        //Used 1 token -> tokens = 2 - 1 = 1
        assertTrue(rateLimiter.allowRequest(freeUser1));

        TimeUnit.SECONDS.sleep(2);
        //Tokens added per second for free user is 2, so Total tokens  after two seconds delay -> 1 + 4 = 5 tokens

        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));
        assertTrue(rateLimiter.allowRequest(freeUser1));

        //All requests limit exhausted
        assertFalse(rateLimiter.allowRequest(freeUser1));
    }


    @Test
    void testCustomUser() {
        User customUser = new User(6, CUSTOM_USER);
        RateParams rateParams = new RateParams(4, new RateRequest(TimeUnit.SECONDS, 5));
        updateSubscriptionConfiguration(CUSTOM_USER, rateParams);

        long count = getSubscriptionTypeRateParamsMap().get(CUSTOM_USER)
                                                       .rateRequest()
                                                       .count();
        boolean expectedResponse = IntStream.iterate(0, i -> i + 1)
                                            .limit(count * 3)
                                            .allMatch(index ->
                                                              (index < Math.min(rateParams.capacity(),
                                                                                rateParams.rateRequest()
                                                                                          .count()
                                                                               )) == rateLimiter.allowRequest(
                                                                      customUser)
                                                     );
        assertTrue(expectedResponse);
    }

    @Test
    void testBasicUserRateLimiting() {
        testUserTypeRateLimiting(BASIC_USER, basicUser);
    }

    @Test
    void testPremiumUserRateLimiting() {
        testUserTypeRateLimiting(PREMIUM_USER, premiumUser);
    }

    @Test
    void testBusinessUserRateLimiting() {
        testUserTypeRateLimiting(BUSINESS_USER, businessUser);
    }

    @Test
    void testTryingUpdatingPredefinedSubscriptionTypeShouldThrowException() {
        RateRequest rateRequest = new RateRequest(TimeUnit.SECONDS, 4);
        RateParams rateParams = new RateParams(50, rateRequest);
        assertThrows(RateParamsUpdateNotAllowedException.class, () -> updateSubscriptionConfiguration(BASIC_USER,
                                                                                                      rateParams
                                                                                                     ));
    }

    @Test
    void testTryingUpdatingInvalidCapacityShouldThrowException() {
        RateRequest rateRequest = new RateRequest(TimeUnit.SECONDS, 4);
        RateParams rateParams = new RateParams(-10, rateRequest);
        assertThrows(InvalidRateParams.class, () -> updateSubscriptionConfiguration(CUSTOM_USER, rateParams));
    }

    @Test
    void testTryingUpdatingInvalidRateRequestShouldThrowException() {
        RateParams rateParams = new RateParams(110, null);
        assertThrows(InvalidRateParams.class, () -> updateSubscriptionConfiguration(CUSTOM_USER, rateParams));
    }

    @Test
    void testTryingUpdatingInvalidCountShouldThrowException() {
        RateRequest rateRequest = new RateRequest(TimeUnit.SECONDS, -10);
        RateParams rateParams = new RateParams(10, rateRequest);
        assertThrows(InvalidRateParams.class, () -> updateSubscriptionConfiguration(CUSTOM_USER, rateParams));
    }
}
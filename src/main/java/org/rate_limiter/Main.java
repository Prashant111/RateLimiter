package org.rate_limiter;

import org.rate_limiter.algo.RateLimiter;
import org.rate_limiter.algo.TokenBucketRateLimiter;

import java.util.Random;

import static org.rate_limiter.SubscriptionType.BASIC_USER;

public class Main {
    public static void main(String[] args) {
//        RateLimiter rateLimiter = new TokenBucketRateLimiter(); // 10 tokens, 2 tokens added per second
//
//        for (int i = 0; i < 15; i++) {
//            Integer userId = new Random().nextInt(10);
//            User user = new User(userId, BASIC_USER);
//            if (rateLimiter.allowRequest(user)) {
//                System.out.println("Request allowed for user " + userId);
//            } else {
//                System.out.println("Request denied for user " + userId + " due to rate limiting");
//            }
//            try {
//                Thread.sleep(500); // Simulate requests over time
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
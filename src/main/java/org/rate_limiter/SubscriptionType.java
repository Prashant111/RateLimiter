package org.rate_limiter;

public enum SubscriptionType {
    FREE_USER("Free User"),
    BASIC_USER("Basic User"),
    PREMIUM_USER("Premium User"),
    BUSINESS_USER("Business User");

    private final String displayName;

    SubscriptionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

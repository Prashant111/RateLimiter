package org.rate_limiter;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public record PaidCredit(AtomicLong credits, LocalDateTime allocationTime) {
}

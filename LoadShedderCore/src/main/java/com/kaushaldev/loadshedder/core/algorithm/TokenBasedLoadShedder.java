package com.kaushaldev.loadshedder.core.algorithm;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Load shedder class to shed excess load and throttle calls.
 *
 * It uses bucket of token to ensure no more that rate limit entries per second.
 */
public class TokenBasedLoadShedder implements LoadShedder {
    private static final long MILLIS_IN_ONE_SECOND = Duration.ofSeconds(1).toMillis();

    private long rateLimitPerSecond;
    private Queue<Long> tokenBucket;

    public TokenBasedLoadShedder(final long rateLimitPerSecond) {

        this.rateLimitPerSecond = rateLimitPerSecond;
        this.tokenBucket = new ConcurrentLinkedDeque<>();
    }

    @Override
    public synchronized boolean throttle() {
        boolean result = false;
        final long currentEpochMilli = ZonedDateTime.now().toInstant().toEpochMilli();

        if (tokenBucket.size() >= rateLimitPerSecond) {
            if (isEldestTokenExpired(currentEpochMilli)) {
                this.tokenBucket.remove();
                this.tokenBucket.add(currentEpochMilli);
                result = true;
            }
        } else {
            this.tokenBucket.add(currentEpochMilli);
            result = true;
        }

        return result;
    }

    private boolean isEldestTokenExpired(final long currentEpochMilli ) {
        boolean result = false;

        if (this.tokenBucket.size() > 0) {
            final long eldestEntry = this.tokenBucket.peek();

            if ((currentEpochMilli - eldestEntry) > MILLIS_IN_ONE_SECOND) {
                result = true;
            }
        }

        return result;
     }
}

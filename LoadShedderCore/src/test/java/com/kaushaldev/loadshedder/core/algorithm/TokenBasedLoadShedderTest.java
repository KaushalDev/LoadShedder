package com.kaushaldev.loadshedder.core.algorithm;

import com.kaushaldev.loadshedder.core.algorithm.LoadShedder;
import com.kaushaldev.loadshedder.core.algorithm.TokenBasedLoadShedder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class TokenBasedLoadShedderTest {
    private static final int rateLimitPerSecond = 10;
    private LoadShedder tokenBasedLoadShedder;

    @BeforeEach
    public void setup() {
        tokenBasedLoadShedder = new TokenBasedLoadShedder(rateLimitPerSecond);
    }

    @Test
    public void throttle_shouldReturnTrue_whenTokenAvailable() {
        Assertions.assertTrue(tokenBasedLoadShedder.throttle());
    }

    @Test
    public void throttle_shouldReturnFalse_whenTokenNotAvailable() {
        IntStream.rangeClosed(1, rateLimitPerSecond).forEach(i -> tokenBasedLoadShedder.throttle());
        Assertions.assertFalse(tokenBasedLoadShedder.throttle());
    }

    @Test
    public void throttle_shouldReturnFalse_whenTokenFinishedDuringParallelProcessing() {
        final AtomicInteger successCounter = new AtomicInteger(0);
        final AtomicInteger failureCounter = new AtomicInteger(0);

        final int totalRequest = 100;

        IntStream.rangeClosed(1, totalRequest)
                 .parallel()
                 .forEach(i -> {
                     if (tokenBasedLoadShedder.throttle()) {
                         successCounter.incrementAndGet();
                     } else {
                         failureCounter.incrementAndGet();
                     }
                 });

        Assertions.assertEquals(rateLimitPerSecond, successCounter.get());
        Assertions.assertEquals(totalRequest - rateLimitPerSecond, failureCounter.get());
    }

    @Test
    public void throttle_shouldThrrotleExccessLoad_whenParallelProcessingWithDelay() throws InterruptedException {
        final AtomicInteger successCounter = new AtomicInteger(0);
        final AtomicInteger failureCounter = new AtomicInteger(0);

        final int totalRequest = 100;

        IntStream.rangeClosed(1, totalRequest)
                 .parallel()
                 .forEach(i -> {
                     if (tokenBasedLoadShedder.throttle()) {
                         successCounter.incrementAndGet();
                     } else {
                         failureCounter.incrementAndGet();
                     }
                 });

        Thread.sleep(1001);

        IntStream.rangeClosed(1, totalRequest)
                 .parallel()
                 .forEach(i -> {
                     if (tokenBasedLoadShedder.throttle()) {
                         successCounter.incrementAndGet();
                     } else {
                         failureCounter.incrementAndGet();
                     }
                 });

        Assertions.assertEquals(rateLimitPerSecond * 2, successCounter.get());
        Assertions.assertEquals((totalRequest - rateLimitPerSecond) * 2, failureCounter.get());
    }
}
package interview.lld.interviewmain.ratelimiter;


import interview.corerjava.multithreading.datastructure.ConcurrentHashMap;

import java.util.concurrent.locks.ReentrantLock;

// 2. Fine-Grained Locking  : Thread-safe map , separate lock per bucket
/*
Operation:
    - bucket creation for each client
    - tryAcquire / allowedRequest
    - refill
 */
public class FineGrainedRateLimiter {
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>(); // Thread-safe map
    private final long capacity;
    private final double tokensPerSecond;
    public FineGrainedRateLimiter(long capacity, double tokensPerSecond) {
        this.capacity = capacity;
        this.tokensPerSecond = tokensPerSecond;
    }

    public boolean tryAcquire (String clientId) {
        // Thread-safe map handles concurrent get-or-create atomically
        // No global lock needed for lookup/insertion
        TokenBucket bucket = buckets.computeIfAbsent(clientId, k -> new TokenBucket(capacity, tokensPerSecond));
        return bucket.tryAcquire();
    }

    static class TokenBucket {
        private final ReentrantLock lock = new ReentrantLock(); // separate lock per bucket
        private double tokens;
        private final long capacity;
        private final double tokensPerSecond;
        private long lastRefillTime;

        public TokenBucket(long capacity, double tokensPerSecond) {
            this.capacity = capacity;
            this.tokensPerSecond = tokensPerSecond;
            this.lastRefillTime = System.nanoTime();
            this.tokens = capacity;
        }

        public boolean tryAcquire() {
            //Lock
            lock.lock();
            try {
                refill();
                if (tokens >= 1) {
                    this.tokens -= 1;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }

        private void refill() {
            long now = System.nanoTime();
            double elapsed = (now - lastRefillTime) / 1_000_000_000.0;
            double tokensToAdd = elapsed * tokensPerSecond;
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }
}



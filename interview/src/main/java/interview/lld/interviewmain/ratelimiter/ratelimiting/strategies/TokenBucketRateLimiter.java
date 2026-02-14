package interview.lld.interviewmain.ratelimiter.ratelimiting.strategies;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
Algorithm:      Each user has a TokenBucket with a fixed capacity. Tokens are added to the bucket at a constant refillRate.
                When a request arrives, it attempts to consume one token. If the bucket is empty, the request is denied.
                This allows for bursts of traffic up to the bucket's capacity.

Refill Logic:   The refill method calculates how many tokens should have been generated since the last refill
                and adds them to the bucket, ensuring the total never exceeds capacity
 */
public class TokenBucketRateLimiter implements RateLimiterStrategy {
    private final int capacity;
    private final int refillRatePerSecond;
    private final Map<String, TokenBucket> userBuckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter (int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    @Override
    public boolean allowRequest(String userId) {
        long currentTime = System.currentTimeMillis();
        userBuckets.putIfAbsent(userId, new TokenBucket(capacity, refillRatePerSecond, currentTime));
        TokenBucket bucket = userBuckets.get(userId);
        synchronized (bucket) {
            bucket.refill(currentTime);
            if (bucket.tokens > 0) {
                bucket.tokens--;
                return true;
            } else {
                return false;
            }
        }
    }

    private static class TokenBucket {
        private final int capacity;
        private final int refillRatePerSecond;

        private long lastRefillTimestamp;
        private int tokens;

        public TokenBucket(int capacity, int refillRatePerSecond, long currentTime) {
            this.capacity = capacity;
            this.refillRatePerSecond = refillRatePerSecond;

            this.tokens = capacity;
            this.lastRefillTimestamp = currentTime;
        }

        public void refill(long currentTime) {
            long elapsedTime = currentTime - lastRefillTimestamp;
            int tokensToAdd = (int) ((elapsedTime / 1000.0 ) * refillRatePerSecond);
            if (tokens > 0) {
                tokens = Math.min(capacity, tokens + tokensToAdd);
                lastRefillTimestamp = currentTime;
            }
        }
    }
}

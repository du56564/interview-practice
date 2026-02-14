package interview.corerjava.multithreading.designproblems.ratelimiter;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


// 1. Coarse-grained locking : One lock for everything (Global Lock)
class CoarseGrainedLocking {
    private final Map<String, TokenBucket> buckets = new HashMap<>();
    private final Object lock = new Object();
    private  double tokenPerSecond;
    private  long capacity;

    public boolean tryAcquire (String clientId) {
        synchronized (lock) {
            TokenBucket bucket = buckets.computeIfAbsent(clientId, k -> new TokenBucket(capacity, tokenPerSecond));
            return bucket.allowedRequest();
        }
    }


    static class TokenBucket {
        private final long capacity;
        private double tokens;
        private final double fillRate;
        private Instant refillTime;

        public TokenBucket (long capacity, double fillRate) {
            this.capacity = capacity;
            this.fillRate = fillRate;
            this.tokens = capacity;
            this.refillTime = Instant.now();
        }

        public boolean allowedRequest () {
            refill();
            if (this.tokens >= 1) {
                this.tokens-=1;
                return true;
            }
            return false;
        }

        public void refill () {
            Instant currentTime = Instant.now();
            double tokensToAdd = (currentTime.toEpochMilli() - refillTime.toEpochMilli()) * fillRate / 1000.0; // get in seconds
            tokens = Math.min(capacity, this.tokens + tokensToAdd);
        }


    }
}




package interview.corerjava.multithreading.designproblems.ratelimiter;

/*
Rate limiting is a technique to control how many requests a client can make to a service within a given time window.
Protect services from overload
Fair resource sharing
Cost Control (Cloud service)
Security (DDoS)

Fixed Window	Count requests in fixed time windows (e.g., 100 req/minute)	Simple to implement	Burst at window boundaries
Sliding Window	Rolling time window, smooths fixed window edges	Smoother limits	More complex, needs timestamped logs
Leaky Bucket	Requests "leak" out at constant rate	Smooths traffic perfectly	Can delay requests
Token Bucket	Tokens refill over time, consumed per request	Allows bursts, smooth sustained rate	Slightly more complex

Token Bucket is widely used in production system.


Q. Design a thread-safe rate limiter that caps how many requests a client can make in a given time window.
Requirements:-
    - Per-client limits: (Identified by  API key, user ID, or IP)
    - Atomic check-and-decreament
    - Token refill
    - Efficient at scale

Parameters: Capacity, Refill Rate | example:  Capacity = 10 tokens, Refill Rate = 2 tokens/second.
Formula:
tokens_to_add = refill_rate × time_elapsed
new_tokens = min(current_tokens + tokens_to_add, capacity)

Challenges:-
    - Token Consumption Rate
    - Double Bucket Creation
    - Double Refill

Synchronization Strategy
    - Coarse-grained locking : One lock for everything
    - Fine-grained locking : One lock per client
    - Lock-free with CAS : No lock at all



 */
// 1. Coarse-Grained Locking : A single lock protects all operations. - Simple (10K/s)
// 2. Fine-Grained Locking  : Thread-safe map , separate lock per bucket - Preferred (100K/s)
// 3. Lock-Free with CAS - Complex (500K/s)


import java.util.concurrent.ConcurrentHashMap;

class TokenBucket {
    private long tokens;
    private final long capacity;
    private final double refillRate;
    private long lastRefillTime;

    public TokenBucket(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillTime = System.nanoTime();
    }

    public synchronized boolean tryAcquire() {
        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastRefillTime) / 1_000_000_000.0;
        long tokensToAdd = (long) (elapsedSeconds * refillRate);

        if (tokensToAdd > 0) {
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }
}

class RateLimiter {
    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final long capacity;
    private final double refillRate;

    public RateLimiter(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    public boolean tryAcquire(String clientId) {
        TokenBucket bucket = buckets.computeIfAbsent(
            clientId, 
            id -> new TokenBucket(capacity, refillRate)
        );
        return bucket.tryAcquire();
    }
}
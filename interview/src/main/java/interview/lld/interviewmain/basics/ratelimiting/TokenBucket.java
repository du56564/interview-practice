package interview.lld.interviewmain.basics.ratelimiting;

import java.time.Instant;

/*
Imagine a bucket that holds tokens.
The bucket has a maximum capacity of tokens.
Tokens are added to the bucket at a fixed rate (e.g., 10 tokens per second).
When a request arrives, it must obtain a token from the bucket to proceed.
If there are enough tokens, the request is allowed and tokens are removed.
If there aren't enough tokens, the request is dropped.
 */
public class TokenBucket {
    private final long capacity;        // Maximum number of tokens the bucket can hold
    private final double fillRate;      // Rate at which tokens are added to the bucket (tokens per second)
    private double tokens;              // Current number of tokens in the bucket
    private Instant lastRefillTimestamp; // Last time we refilled the bucket

    public TokenBucket(long capacity, double fillRate) {
        this.capacity = capacity;
        this.fillRate = fillRate;
        this.tokens = capacity;  // Start with a full bucket
        this.lastRefillTimestamp = Instant.now();
    }

    public synchronized boolean allowRequest(int tokens) {
        refill();  // First, add any new tokens based on elapsed time

        if (this.tokens < tokens) {
            return false;  // Not enough tokens, deny the request
        }

        this.tokens -= tokens;  // Consume the tokens
        return true;  // Allow the request
    }

    private void refill() {
        Instant now = Instant.now();
        // Calculate how many tokens to add based on the time elapsed
        double tokensToAdd = (now.toEpochMilli() - lastRefillTimestamp.toEpochMilli()) * fillRate / 1000.0;//(rate/ 1000 ms)
        this.tokens = Math.min(capacity, this.tokens + tokensToAdd);  // Add tokens, but don't exceed capacity
        this.lastRefillTimestamp = now;
    }
}

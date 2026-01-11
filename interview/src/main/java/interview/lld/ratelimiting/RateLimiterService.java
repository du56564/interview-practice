package interview.lld.ratelimiting;

import interview.lld.ratelimiting.strategies.RateLimiterStrategy;

// This is Singleton Pattern
// Uses Strategy Pattern because of using RateLimiterStrategy
// Facade to hide implementation from Client
/*
 What “In-Memory Rate Limiter” means (THIS)
	•	All rate-limit data lives inside JVM memory
	•	Uses:
	•	ConcurrentHashMap
	•	synchronized / Atomic / locks
	•	Works per user / per API key
	•	Not distributed (resets on restart)
 */
class RateLimiterService {
    private static RateLimiterService instance;
    private RateLimiterStrategy rateLimiterStrategy;

    private RateLimiterService() {}

    public static synchronized RateLimiterService getInstance() {
        if (instance == null) {
            instance = new RateLimiterService();
        }
        return instance;
    }

    public void setRateLimitingStrategy(RateLimiterStrategy rateLimitingStrategy) {
        this.rateLimiterStrategy = rateLimitingStrategy;
    }

    public void handleRequest (String userId) {
        if (rateLimiterStrategy.allowRequest(userId)) {
            System.out.println("Request from user " + userId + " is allowed");
        } else {
            System.out.println("Request from user " + userId + " is rejected: Rate limit exceeded");
        }
    }

}

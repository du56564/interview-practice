package interview.lld.ratelimiting.strategies;

public interface RateLimiterStrategy {
    boolean allowRequest(String userId);
}

package interview.lld.interviewmain.ratelimiter.ratelimiting.strategies;

public interface RateLimiterStrategy {
    boolean allowRequest(String userId);
}

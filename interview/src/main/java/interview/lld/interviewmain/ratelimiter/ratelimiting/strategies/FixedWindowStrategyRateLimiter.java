package interview.lld.interviewmain.ratelimiter.ratelimiting.strategies;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
//maxRequest / Given window time (ms)
public class FixedWindowStrategyRateLimiter implements RateLimiterStrategy {
    private final int maxRequests;
    private final long windowSizeInMillis;
    private final Map<String, UserRequestInfo> userRequestMap = new ConcurrentHashMap<>();

    public FixedWindowStrategyRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis * 1000;
    }

    @Override
    public boolean allowRequest(String userId) {
        long currentTime = System.currentTimeMillis();
        userRequestMap.putIfAbsent(userId, new UserRequestInfo(currentTime)); //IF null then add else return current value
        UserRequestInfo requestInfo = userRequestMap.get(userId);
        synchronized (this) {
            if (currentTime - requestInfo.windowStart >= windowSizeInMillis) {
                requestInfo.reset(currentTime);
            }
            if (requestInfo.requestCount.get() < maxRequests) {
                requestInfo.requestCount.incrementAndGet();
                return true;
            } else {
                return false;
            }
        }
    }

    private static class UserRequestInfo {
        long windowStart;
        AtomicInteger requestCount;
        public UserRequestInfo (long startTime) {
            this.windowStart = startTime;
            this.requestCount = new AtomicInteger(0);
        }

        void reset (long newStart) {
            this.windowStart = newStart;
            this.requestCount.set(0);
        }
    }
}

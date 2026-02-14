package interview.lld.interviewmain.ratelimiter.ratelimiting;

import interview.lld.interviewmain.ratelimiter.ratelimiting.strategies.FixedWindowStrategyRateLimiter;
import interview.lld.interviewmain.ratelimiter.ratelimiting.strategies.TokenBucketRateLimiter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RateLimiterMain {

    static void main() {
        String userId = "user123";
        System.out.println("=== Fixed Window Demo ===");
        runFixedWindowDemo(userId);
        //OR
        System.out.println("\n=== Token Bucket Demo ===");
        runTokenBucketDemo(userId);
    }

    private static void runFixedWindowDemo(String userId) {
        int maxRequest = 5;
        int windowSeconds = 10;

        FixedWindowStrategyRateLimiter fixedWindowStrategyRateLimiter = new FixedWindowStrategyRateLimiter(maxRequest, windowSeconds);
        RateLimiterService service = RateLimiterService.getInstance();
        service.setRateLimitingStrategy(fixedWindowStrategyRateLimiter);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 10; i++) {
            executor.submit(() -> service.handleRequest(userId));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        executor.shutdown();
    }

    private static void runTokenBucketDemo(String userId) {
        int capacity = 5;
        int refillRate = 1; // 1 token per second
        TokenBucketRateLimiter tokenBucketRateLimiter = new TokenBucketRateLimiter(capacity, refillRate);
        RateLimiterService service = RateLimiterService.getInstance();
        service.setRateLimitingStrategy(tokenBucketRateLimiter);

        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            executor.submit(() -> service.handleRequest(userId));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        executor.shutdown();
    }

}

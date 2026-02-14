package interview.corerjava.multithreading;

import java.io.IOException;
import java.net.HttpRetryException;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class CommonPatternExponentialBackOff {

    private static final ScheduledExecutorService scheduler =
        Executors.newScheduledThreadPool(1);

    public static <T> CompletableFuture<T> retryWithBackoff(
            Supplier<CompletableFuture<T>> operation,
            int maxAttempts,
            Duration initialDelay,
            double backoffMultiplier) {

        return attemptWithRetry(operation, maxAttempts, initialDelay,
            backoffMultiplier, 1, null);
    }

    private static <T> CompletableFuture<T> attemptWithRetry(
            Supplier<CompletableFuture<T>> operation,
            int maxAttempts,
            Duration delay,
            double multiplier,
            int attempt,
            Throwable lastError) {

        if (attempt > maxAttempts) {
            return CompletableFuture.failedFuture(
                new Exception("Failed after " + maxAttempts +
                    " attempts", lastError));
        }

        return operation.get().handle((result, ex) -> {
            if (ex == null) {
                return CompletableFuture.completedFuture(result);
            }

            // Check if error is retryable
            if (!isRetryable(ex)) {
                return CompletableFuture.<T>failedFuture(ex);
            }

            System.out.printf("Attempt {} failed, retrying in {}ms: {}",
                attempt, delay.toMillis(), ex.getMessage());

            // Schedule retry after delay
            CompletableFuture<T> retryFuture = new CompletableFuture<>();
            scheduler.schedule(() -> {
                attemptWithRetry(operation, maxAttempts,
                    Duration.ofMillis((long)(delay.toMillis() * multiplier)),
                    multiplier, attempt + 1, ex)
                    .whenComplete((r, e) -> {
                        if (e != null) retryFuture.completeExceptionally(e);
                        else retryFuture.complete(r);
                    });
            }, delay.toMillis(), TimeUnit.MILLISECONDS);

            return retryFuture;
        }).thenCompose(Function.identity());
    }

    private static boolean isRetryable(Throwable ex) {
        // Retry on transient errors, not on validation errors
        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
        return cause instanceof IOException
            || cause instanceof TimeoutException;
            //|| (cause instanceof Exception &&
                //((HttpRetryException) cause) >= 500);
    }
}
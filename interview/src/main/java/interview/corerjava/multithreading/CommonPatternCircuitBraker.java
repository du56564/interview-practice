package interview.corerjava.multithreading;


import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class CommonPatternCircuitBraker<T> {

    private enum State { CLOSED, OPEN, HALF_OPEN }

    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final int failureThreshold;
    private final Duration openDuration;
    private volatile Instant openedAt;

    public CommonPatternCircuitBraker(int failureThreshold, Duration openDuration) {
        this.failureThreshold = failureThreshold;
        this.openDuration = openDuration;
    }

    public CompletableFuture<T> execute(Supplier<CompletableFuture<T>> operation) {
        State currentState = state.get();

        // Circuit is open - fail fast
        if (currentState == State.OPEN) {
            if (shouldAttemptReset()) {
                state.compareAndSet(State.OPEN, State.HALF_OPEN);
            } else {
               // return CompletableFuture.failedFuture(
                 //       new CircuitOpenException("Circuit breaker is open"));
            }
        }

        return operation.get()
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        handleFailure();
                    } else {
                        handleSuccess();
                    }
                });
    }

    private void handleSuccess() {
        failureCount.set(0);
        state.set(State.CLOSED);
    }

    private void handleFailure() {
        if (failureCount.incrementAndGet() >= failureThreshold) {
            state.set(State.OPEN);
            openedAt = Instant.now();
        }
    }

    private boolean shouldAttemptReset() {
        return openedAt != null &&
                Instant.now().isAfter(openedAt.plus(openDuration));
    }
}
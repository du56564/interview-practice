package interview.corerjava.multithreading;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/*
A critical section is a region of code that reads or writes shared state (for example: a counter, a map, a queue, a file, or a database row)
Protecting Critical Sections
Solution 1: Mutex/Locks
Solution 2: Atomic Operations
Solution 3: Immutability
 */
// Model 1: Shared Memory
// UNSAFE: Race condition
class UnsafeCounter {
    private int count = 0;

    public void increment() {
        count++;  // Not atomic!
    }

    public int getCount() {
        return count;
    }
}

// SAFE: Using synchronized
class SafeCounter {
    private int count = 0;

    public synchronized void increment() {
        count++;  // Now atomic with respect to other synchronized methods
    }

    public synchronized int getCount() {
        return count;
    }
}

// SAFE: Using explicit lock
class SafeCounterWithLock {
    private int count = 0;
    private final Object lock = new Object();

    public void increment() {
        synchronized (lock) {
            count++;
        }
    }

    public int getCount() {
        synchronized (lock) {
            return count;
        }
    }
}
class AtomicCounter {
    // Atomic operations provide lock-free thread safety.
    private final AtomicInteger count = new AtomicInteger(0);
    public void increament() {
        count.incrementAndGet();
    }

    public int getCount() {
        return count.get();
    }

}

// Immutable class - no synchronization needed : used final keyword
// If data never changes, it can't have race conditions. Immutable objects are inherently thread-safe.
final class ImmutablePoint {
    private final int x;
    private final int y;

    public ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    // Returns a new object instead of modifying
    public ImmutablePoint move(int dx, int dy) {
        return new ImmutablePoint(x + dx, y + dy);
    }
}

//Model 2: MessagePassing used in communication pattern
class MessagePassingDemo {
    static void main() throws InterruptedException {
        BlockingQueue<String> channel = new LinkedBlockingQueue<>();
        Thread producer = new Thread(() -> {
            try {
                channel.put("Hello");
                channel.put("Multi-Threading class");
                channel.put("I am producer");
                channel.put("emmiting events");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String msg = channel.take();
                    if ("Done".equals(msg)) break;
                    System.out.println("Received: "+ msg);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }
}

// Model 3: Event-Driven (Async/Await)
// Event-driven concurrency uses a single thread that responds to events from a queue.
// Instead of blocking, operations register callbacks that execute when events complete.
/*
The event loop:
Waits for events in the queue
Dispatches each event to its handler
Handler executes (should be quick, non-blocking)
Handler may register new callbacks for async operations
Loop continues

Single-threaded: One thread handles all events. No locks needed for most code.
Non-blocking I/O: All I/O operations are asynchronous. Instead of blocking, you register a callback or use async/await.
High concurrency: Can handle thousands of connections with minimal memory (no thread per connection).
 */
class AsyncDemo {
    static void main() {
        CompletableFuture<String> future = fetchDataAsync("url1")
                .thenCompose(data1 -> fetchDataAsync("url2")
                        .thenApply(data2 -> data1 +" "+data2))
                .thenApply(String::toUpperCase);
        // Non-blocking : register what to do when complete
        future.thenAccept(result -> System.out.println("Result:" + result));

        //Or block and wait
        String result = future.join();
    }

    private static CompletableFuture<String> fetchDataAsync(String url) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
            return "Data from" + url;
        });
    }
}


// Data parallelism
class DataParallelismDemo {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

        // Sequential
        int sumSeq = numbers.stream()
                .map(n -> n * n)
                .reduce(0, Integer::sum);

        // Parallel - same code, automatic parallelism!
        int sumPar = numbers.parallelStream()
                .map(n -> n * n)
                .reduce(0, Integer::sum);

        System.out.println("Sum of squares: " + sumPar);  // 204
    }
}

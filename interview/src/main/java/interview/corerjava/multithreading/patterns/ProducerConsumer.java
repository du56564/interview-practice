package interview.corerjava.multithreading.patterns;

import java.time.Instant;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

// Core implementation
class BoundedBuffer<T> {
    private final Queue<T> queue;
    private final int capacity;

    public BoundedBuffer(int bufferSize) {
        this.queue = new LinkedList<>();
        this.capacity = bufferSize;
    }

    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait();
        }
        queue.offer(item);
        notifyAll();
    }

    public synchronized T take () throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.remove();
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }
}

// Using library
public class ProducerConsumer {
    static void main() throws InterruptedException {
        BlockingQueue<String> buffer = new LinkedBlockingQueue<>();

        // consumer
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String item = buffer.take();
                    if (item.equals("POISON")) break;
                    System.out.println("Consumed:"+ item);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();


        // Produce items
        for (int i = 0; i < 20; i++) {
            buffer.put("Item-" + i);  // Blocks if full
            System.out.println("Produced: Item-" + i);
        }

        // Send poison pill to signal shutdown
        buffer.put("POISON");
        consumer.join();
    }
}

/*
Scenario: Log Processing Pipeline'
Requirements:
    Handle bursts of logs without dropping data
    Decouple log generation from storage (don't slow down apps)
    Scale consumers independently of producers
    Graceful shutdown without losing buffered logs
*/
class LogPipeline {
    private final BlockingQueue<RawLog> rawBuffer;
    private final BlockingQueue<ParsedLog> parsedBuffer;
    private final ExecutorService parsers;
    private final ExecutorService storers;
    private volatile boolean shutdown = false;

    public LogPipeline(int bufferSize, int parserCount, int storerCount) {
        this.rawBuffer = new ArrayBlockingQueue<>(bufferSize);
        this.parsedBuffer = new ArrayBlockingQueue<>(bufferSize);
        this.parsers = Executors.newFixedThreadPool(parserCount);
        this.storers = Executors.newFixedThreadPool(storerCount);

        // Start parser workers
        for (int i = 0; i < parserCount; i++) {
            parsers.submit(this::parserWorker);
        }

        // Start storage workers
        for (int i = 0; i < storerCount; i++) {
            storers.submit(this::storageWorker);
        }
    }

    // Called by application servers
    public boolean submit(String logLine) {
        if (shutdown) return false;
        RawLog log = new RawLog(logLine, Instant.now());
        return rawBuffer.offer(log);  // Non-blocking, returns false if full
    }

    private void parserWorker() {
        while (!shutdown || !rawBuffer.isEmpty()) {
            try {
                RawLog raw = rawBuffer.poll(100, TimeUnit.MILLISECONDS);
                if (raw == null) continue;

                ParsedLog parsed = parse(raw);
                parsedBuffer.put(parsed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void storageWorker() {
        while (!shutdown || !parsedBuffer.isEmpty()) {
            try {
                ParsedLog log = parsedBuffer.poll(100, TimeUnit.MILLISECONDS);
                if (log == null) continue;

                storeInElasticsearch(log);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void shutdown() throws InterruptedException {
        shutdown = true;

        // Wait for buffers to drain
        while (!rawBuffer.isEmpty() || !parsedBuffer.isEmpty()) {
            Thread.sleep(100);
        }

        parsers.shutdown();
        storers.shutdown();
        parsers.awaitTermination(30, TimeUnit.SECONDS);
        storers.awaitTermination(30, TimeUnit.SECONDS);
    }

    private ParsedLog parse(RawLog raw) {
        // Parse log line into structured format
        String[] parts = raw.line().split(" ", 3);
        return new ParsedLog(
                parts.length > 0 ? parts[0] : "UNKNOWN",
                parts.length > 1 ? parts[1] : "INFO",
                parts.length > 2 ? parts[2] : raw.line(),
                raw.receivedAt()
        );
    }

    private void storeInElasticsearch(ParsedLog log) {
        // Simulate Elasticsearch write
        try {
            Thread.sleep(50);  // Simulate network latency
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

record RawLog(String line, Instant receivedAt) {}
record ParsedLog(String source, String level, String message, Instant timestamp) {}

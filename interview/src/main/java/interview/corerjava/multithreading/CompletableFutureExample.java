package interview.corerjava.multithreading;

import java.util.concurrent.CompletableFuture;

/*
A CompletableFuture represents a future result of an asynchronous computation
CompletableFuture is designed to allow you to build complex asynchronous workflows, including chaining multiple tasks together and handling their results.
 */
public class CompletableFutureExample {
    public static void main(String[] args) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            // Simulate a long-running task
            sleep(2000);
            return "Hello, CompletableFuture!";
        });

        // This will block until the computation is complete and print the result
        future.thenAccept(result -> System.out.println(result));
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}


class ChainingExample {
    public static void main(String[] args) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            sleep(2000);
            return "Data fetched";
        });

        future.thenApply(data -> {
            // Process the data
            return data + " and processed";
        }).thenAccept(result -> {
            // Save the result
            System.out.println(result + " saved!");
        });
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
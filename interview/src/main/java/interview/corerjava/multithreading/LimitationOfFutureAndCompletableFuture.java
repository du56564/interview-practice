package interview.corerjava.multithreading;


/*

ExecutorService executor = Executors.newFixedThreadPool(4);

// Submit a task, get a Future
Future<String> future = executor.submit(() -> {
    Thread.sleep(2000);
    return "Result";
});

Problem 1: get() blocks
String result = future.get();  // Current thread is stuck here for 2 seconds

Problem 2: No composition
How do you say "when this completes, do that"?
You can't. You have to block.

Problem 3: No manual completion
What if you want to complete with a cached value? You can't.

Problem 4: No exception handling pipeline
If the computation throws, you only find out when calling get()

 */


import java.util.concurrent.CompletableFuture;

public class LimitationOfFutureAndCompletableFuture {
    static void main() {

        // Below : These methods transform the result when the stage completes.
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> "hello");

        //thenApply: Transform the Value
        CompletableFuture<String> transformed = cf.thenApply(s -> s.toUpperCase());
        CompletableFuture<Integer> length = cf.thenApply(String::toUpperCase)
                                            .thenApply(String::length);

        //thenAccept : Consume the value
        CompletableFuture<String> cfAccept = CompletableFuture.supplyAsync(() -> "hello");

        CompletableFuture<Void> consume = cfAccept.thenAccept(s -> {
            System.out.println(s);
        });

        //thenRun: Run After Completion
        CompletableFuture<String> cfRun = CompletableFuture.supplyAsync(() -> "hello");

        CompletableFuture<Void> finished = cfRun.thenRun(() -> {
            System.out.println("Task completed!");
        });

        //thenCompose : saves the day. It "flattens" the nested future, similar to how flatMap works on streams or optionals.


    }
}

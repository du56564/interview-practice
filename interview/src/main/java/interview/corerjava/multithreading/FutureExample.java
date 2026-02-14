package interview.corerjava.multithreading;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Future : once submitted a Callable task, you receive a Future object in return. get(), isDone(), cancel(), isCancelled()
// Asynchronous Computation: Use Callable and Future for complex computations that can run in the background without blocking your main application flow.
// Parallel Processing
public class FutureExample {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        
        Callable<String> task = () -> {
            Thread.sleep(2000);
            return "Task completed!";
        };

        Future<String> future = executorService.submit(task); // Submit the task

        // Check if the task is done
        while (!future.isDone()) {
            System.out.println("Task is still running...");
            Thread.sleep(1000); // Wait for a second before checking again
        }

        try {
            // Get the result of the task
            String result = future.get();
            System.out.println(result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }
}
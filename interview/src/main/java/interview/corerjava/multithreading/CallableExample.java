package interview.corerjava.multithreading;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
// Callable is a functional interface that represents a task that can be executed by a thread.
// Callable can return results
public class CallableExample {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        
        Callable<Integer> task = () -> {
            // Simulate a long-running task
            Thread.sleep(200);
            return 42; // Return the result
        };

        try {
            Integer result = executorService.submit(task).get(); // Submit the task and get the result
            System.out.println("The result is: " + result);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown(); // Always shut down the executor service
        }
    }
}
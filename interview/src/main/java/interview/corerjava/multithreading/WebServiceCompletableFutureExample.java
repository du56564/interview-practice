package interview.corerjava.multithreading;

import java.util.concurrent.CompletableFuture;

public class WebServiceCompletableFutureExample {
    public static void main(String[] args) {
        CompletableFuture<String> userFuture = CompletableFuture.supplyAsync(() -> {
            // Simulate API call to fetch user
            sleep(100);
            return "User data";
        });

        CompletableFuture<String> orderFuture = CompletableFuture.supplyAsync(() -> {
            // Simulate API call to fetch order
            sleep(150);
            return "Order data";
        });

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(userFuture, orderFuture);
        
        combinedFuture.thenRun(() -> {
            try {
                System.out.println(userFuture.get());
                System.out.println(orderFuture.get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        combinedFuture.join();
    }

    private static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
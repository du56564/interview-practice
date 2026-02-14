package interview.corerjava.multithreading;


import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import java.util.concurrent.*;

public class CommonPatternFallback {

    private final PricingService pricingService = new PricingService();

    // Pattern 1: Timeout with Fallback
    public CompletableFuture<PricingData> getPricingWithTimeout(String productId) {
        return pricingService.fetchPricing(productId)
                .orTimeout(2, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    if (ex instanceof TimeoutException ||
                            ex.getCause() instanceof TimeoutException) {
                        System.out.println("Timeout occurred for product: " + productId);
                        return PricingData.DEFAULT;
                    }

                    throw new CompletionException(ex);
                });
    }

    public static void main(String[] args) {
        CommonPatternFallback app =
                new CommonPatternFallback();

        app.getPricingWithTimeout("ABC123")
                .thenAccept(System.out::println)
                .join();
    }
}

class PricingData {

    private final String productId;
    private final double price;

    public static final PricingData DEFAULT = new PricingData("UNKNOWN", 0.0);

    public PricingData(String productId, double price) {
        this.productId = productId;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "PricingData{" +
                "productId='" + productId + '\'' +
                ", price=" + price +
                '}';
    }
}


class PricingService {

    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final
    Random random = new Random();

    public CompletableFuture<PricingData> fetchPricing(String productId) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simulate external API latency (0–3 seconds)
                int delay = random.nextInt(3000);
                Thread.sleep(delay);

                System.out.println("Fetched pricing in " + delay + " ms");

                return new PricingData(productId, 100 + random.nextDouble() * 50);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Pricing fetch interrupted", e);
            }
        }, executor);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
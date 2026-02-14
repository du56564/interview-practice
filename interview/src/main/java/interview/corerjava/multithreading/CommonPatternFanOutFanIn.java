package interview.corerjava.multithreading;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/*
This pattern is everywhere: search across multiple backends, aggregate data from several microservices, process items in parallel.
Start multiple operations at once, then collect all results.

 */
public class CommonPatternFanOutFanIn {
    /*
    public CompletableFuture<SearchResults> searchAllProviders(String query) {
        // Define search providers
        List<SearchProvider> providers = List.of(
                new GoogleSearchProvider(),
                new BingSearchProvider(),
                new DuckDuckGoProvider()
        );

        // Fan-out: start all searches in parallel
        List<CompletableFuture<ProviderResult>> futures = providers.stream()
                .map(provider -> searchWithTimeout(provider, query))
                .toList();

        // Fan-in: collect all results
        return CompletableFuture
                .allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> {
                    List<ProviderResult> results = futures.stream()
                            .map(CompletableFuture::join)
                            .filter(result -> !result.hasError())  // Filter out failures
                            .toList();

                    return new SearchResults(query, results, providers.size());
                });
    }

    private CompletableFuture<ProviderResult> searchWithTimeout(
            SearchProvider provider, String query) {

        return CompletableFuture
                .supplyAsync(() -> provider.search(query), ioExecutor)
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.warn("Provider {} failed: {}", provider.getName(), ex.getMessage());
                    return ProviderResult.error(provider.getName(), ex);
                });
    }

         */
}

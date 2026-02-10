package interview.lld.urlshortner;

/*
Design URL Shortener
- converts a long URL into a shorter
- contain a short domain name and a unique identifier

Requirements
    Functional
        - generate short url and redirects to original url
        - custom alias
        - custom url expiration policy by user
        - analytics: basic click count
    Non-Functional
        - Uniqueness
        - Extensibility
        - Maintainability

Approach
Each time a new URL is submitted, we get a unique, incrementing ID from a counter (e.g., 1, 2, 3, ...).
We then convert this numerical ID into a short, alphanumeric string (base62 encoding).
A base-62 system uses 62 characters: [0-9], [a-z], and [A-Z].

    Entity
        - EventType : URL_CREATED, URL_ACCESSED
        - ShortenedUrl : (Data class)
        - KeyGenerationStrategy
        - URLRepository
        - Observer
        - URLShortenerService
    Design Pattern
        - Builder Pattern (ShortenedUrl data)
        - Strategy Pattern (Algo)
        - Repository Pattern (CRUD shorten data)
        - Observer Pattern (Analytics: Notify System)
        - Facade Pattern (Central Engine, svc)

*/

import interview.lld.urlshortner.observer.AnalyticsService;
import interview.lld.urlshortner.repository.InMemoryUrlRepository;
import interview.lld.urlshortner.strategy.Base62Strategy;
import interview.lld.urlshortner.strategy.RandomStrategy;

import java.util.Optional;

public class MainUrlShortner {
    static void main() {
        URLShortenerService shortenerService = URLShortenerService.getInstance();
        shortenerService.configure("http://short.ly/", new InMemoryUrlRepository(), new RandomStrategy());
        shortenerService.addObserver(new AnalyticsService());

        System.out.println("-- Init Url Shortener Service --- ");

        // --- 2. Usage Phase ---
        String originalUrl1 = "https://www.verylongurl.com/with/lots/of/path/segments/and/query/params?id=123&user=test";
        System.out.println("Shortening: " + originalUrl1);
        String shortUrl1 = shortenerService.shorten(originalUrl1);
        System.out.println("Generated Short URL: " + shortUrl1);
        System.out.println();

        // Shorten the same URL again
        System.out.println("Shortening the same URL again...");
        String shortUrl2 = shortenerService.shorten(originalUrl1);
        System.out.println("Generated Short URL: " + shortUrl2);
        if (shortUrl1.equals(shortUrl2)) {
            System.out.println("SUCCESS: The system correctly returned the existing short URL.\n");
        }

        // Shorten a different URL
        String originalUrl2 = "https://www.anotherdomain.com/page.html";
        System.out.println("Shortening: " + originalUrl2);
        String shortUrl3 = shortenerService.shorten(originalUrl2);
        System.out.println("Generated Short URL: " + shortUrl3);
        System.out.println();

        // --- 3. Resolution Phase ---
        System.out.println("--- Resolving and Tracking Clicks ---");

        // Resolve the first URL multiple times
        resolveAndPrint(shortenerService, shortUrl1);
        resolveAndPrint(shortenerService, shortUrl1);
        resolveAndPrint(shortenerService, shortUrl3);

        // Try to resolve a non-existent URL
        System.out.println("\nResolving a non-existent URL...");
        resolveAndPrint(shortenerService, "http://short.ly/nonexistent");

    }


    private static void resolveAndPrint(URLShortenerService shortener, String shortUrl) {
        Optional<String> resolvedUrl = shortener.resolve(shortUrl);
        if (resolvedUrl.isPresent()) {
            System.out.printf("Resolved %s -> %s%n", shortUrl, resolvedUrl.get());
        } else {
            System.out.printf("No original URL found for %s%n", shortUrl);
        }
    }
}

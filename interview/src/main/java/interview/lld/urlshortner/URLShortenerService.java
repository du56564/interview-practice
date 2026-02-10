package interview.lld.urlshortner;

import interview.lld.urlshortner.builder.ShortenedUrl;
import interview.lld.urlshortner.enums.EventType;
import interview.lld.urlshortner.observer.Observer;
import interview.lld.urlshortner.repository.URLRepository;
import interview.lld.urlshortner.strategy.KeyGenerationStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class URLShortenerService {
    private static URLShortenerService INSTANCE = new URLShortenerService();

    private URLRepository urlRepository;
    private KeyGenerationStrategy keyGenerationStrategy;
    private String domain;
    private static final int MAX_RETRIES = 10;
    private final List<Observer> observers = new ArrayList<>();

    private URLShortenerService () {}

    public static synchronized URLShortenerService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new URLShortenerService();
        }
        return INSTANCE;
    }

    public void configure (String domain, URLRepository repository, KeyGenerationStrategy strategy) {
        this.domain = domain;
        this.urlRepository = repository;
        this.keyGenerationStrategy = strategy;
    }

    public String shorten(String longUrl) {
        Optional<String> existingKey = urlRepository.findKeyByLongURL(longUrl);
        if (existingKey.isPresent()) {
            return domain + existingKey;
        }

        //else generate a new key
        String shortKey = generateUniqueKey();

        // store into db
        ShortenedUrl shortenedUrl = new ShortenedUrl.Builder(longUrl, shortKey).build();
        urlRepository.save(shortenedUrl);

        // notify all users
        notifyObservers(EventType.URL_CREATED, shortenedUrl);

        return domain + shortKey;
    }

    private String generateUniqueKey() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            String potentialKey = keyGenerationStrategy.generateKey(urlRepository.getNextId());
            if (!urlRepository.existsByKey(potentialKey)) {
                return potentialKey;
            }
        }
        // else couldn't generate key after many attempt
        throw new RuntimeException("Failed to generate a unique short key after " + MAX_RETRIES + " attempts.");
    }

    public Optional<String> resolve(String shortURL) {
        if (!shortURL.startsWith(domain)) {
            return Optional.empty();
        }
        String shortKey = shortURL.replace(domain, "");

        if (urlRepository.existsByKey(shortKey)) {
            ShortenedUrl shortenedURL = urlRepository.findByKey(shortKey).get();
            notifyObservers(EventType.URL_ACCESSED, shortenedURL);
            return Optional.of(shortKey);
        }

        return Optional.empty();
    }

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void notifyObservers(EventType eventType, ShortenedUrl shortenedUrl) {
        for (Observer observer : observers) {
            observer.update(eventType, shortenedUrl);
        }
    }



}

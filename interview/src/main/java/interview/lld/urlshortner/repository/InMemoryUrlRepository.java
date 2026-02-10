package interview.lld.urlshortner.repository;

import interview.lld.urlshortner.builder.ShortenedUrl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

//Repostiroy Pattern
public class InMemoryUrlRepository implements URLRepository {
    private final Map<String, ShortenedUrl> keyToUrlMap = new ConcurrentHashMap<>();
    private final Map<String, String> longUrlToKeyMap = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    @Override
    public void save(ShortenedUrl url) {
        keyToUrlMap.put(url.getShortKey(), url);
        longUrlToKeyMap.put(url.getLongUrl(), url.getShortKey());
    }

    @Override
    public Optional<ShortenedUrl> findByKey(String key) {
        ShortenedUrl url = keyToUrlMap.get(key);
        return Optional.ofNullable(url);
    }

    @Override
    public Optional<String> findKeyByLongURL(String longURL) {
        String key = longUrlToKeyMap.get(longURL);
        return Optional.ofNullable(key);
    }

    @Override
    public long getNextId() {
        return idCounter.getAndIncrement();
    }

    @Override
    public boolean existsByKey(String key) {
        return keyToUrlMap.containsKey(key);
    }
}

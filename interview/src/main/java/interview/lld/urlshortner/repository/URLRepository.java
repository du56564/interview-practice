package interview.lld.urlshortner.repository;

import interview.lld.urlshortner.builder.ShortenedUrl;

import java.util.Optional;

public interface URLRepository {
    void save (ShortenedUrl url);
    Optional<ShortenedUrl> findByKey(String key);
    Optional<String> findKeyByLongURL(String longURL);
    long getNextId ();
    boolean existsByKey(String key);
}

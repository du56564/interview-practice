package interview.lld.urlshortner.observer;

import interview.lld.urlshortner.builder.ShortenedUrl;
import interview.lld.urlshortner.enums.EventType;

// Analytics: access count and track url creation
public interface Observer {
    void update (EventType type, ShortenedUrl url);
}

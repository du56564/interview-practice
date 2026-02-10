package interview.lld.urlshortner.builder;

import java.time.LocalDateTime;

public class ShortenedUrl {
    private final String longUrl;
    private final String shortKey;
    private final LocalDateTime creationDate;

    private ShortenedUrl (Builder builder) {
        this.longUrl = builder.longUrl;
        this.shortKey = builder.shortKey;
        this.creationDate = builder.creationDate;
    }

    public String getLongUrl () {
        return longUrl;
    }

    public String getShortKey () {
        return  shortKey;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public static class Builder {
        private final String longUrl;
        private final String shortKey;
        private LocalDateTime creationDate;

        public Builder (String longURL, String shortKey) {
            this.longUrl = longURL;
            this.shortKey = shortKey;
            this.creationDate = LocalDateTime.now();
        }

        public Builder creationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public ShortenedUrl build () {
            return new ShortenedUrl(this);
        }
    }
}

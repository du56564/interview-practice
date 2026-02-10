package interview.lld.urlshortner.strategy;

import java.util.UUID;

public class UUIDStrategy implements KeyGenerationStrategy{
    private static final int KEY_LENGTH = 6;
    @Override
    public String generateKey(long id) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, KEY_LENGTH);
    }
}

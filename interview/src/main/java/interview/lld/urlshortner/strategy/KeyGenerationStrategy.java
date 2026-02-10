package interview.lld.urlshortner.strategy;

public interface KeyGenerationStrategy {
    String generateKey(long id);
}

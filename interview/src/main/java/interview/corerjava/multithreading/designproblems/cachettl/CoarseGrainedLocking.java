package interview.corerjava.multithreading.designproblems.cachettl;


import java.util.HashMap;
import java.util.Map;
// Approach 1: Basic
// Global Lock
// Overhead due to synchronized keyword
public class CoarseGrainedLocking<K, V> {
    private final Map<K, CacheEntry<V>> cache = new HashMap<>();
    private Object lock = new Object();

    public V get(K key) {
        synchronized (lock) {
            CacheEntry<V> entry = cache.get(key);
            if (entry == null || entry.isExpired()) {
                cache.remove(key);  // Lazy cleanup
                return null;
            }
            return entry.getValue();
        }
    }

    public void put(K key, V value, long ttlMillis) {
        synchronized (lock) {
            long expiryTime = System.currentTimeMillis() + ttlMillis;
            cache.put(key, new CacheEntry<>(value, expiryTime));
        }
    }

    static class CacheEntry<V> {
        private final V value;
        private final long expiryTime;

        public CacheEntry(V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }

        public V getValue() {
            return value;
        }
    }
}



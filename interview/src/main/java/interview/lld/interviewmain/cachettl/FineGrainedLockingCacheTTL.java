package interview.lld.interviewmain.cachettl;


import java.util.concurrent.ConcurrentHashMap;

// Approach 2: Intermediate using ConcurrentHashMap
public class FineGrainedLockingCacheTTL<K, V> {
    private final ConcurrentHashMap<K, CacheEntry> cache = new ConcurrentHashMap<>();

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);  // Lazy cleanup
            return null;
        }
        return entry.getValue();
    }

    public void put(K key, V val, long ttlTime) {
        cache.put(key, new CacheEntry(val, System.currentTimeMillis() + ttlTime));
    }

    // Get non-expired entries
    public int getSize() {
        return (int) cache.values().stream().filter(cacheEntry -> !cacheEntry.isExpired()).count();
    }


    static class CacheEntry<V> {
        private final V value;
        private final long expiryTime;

        public CacheEntry (V value, long expiryTime) {
            this.value = value;
            this.expiryTime = expiryTime;
        }

        public V getValue () {
            return value;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }

    }

}


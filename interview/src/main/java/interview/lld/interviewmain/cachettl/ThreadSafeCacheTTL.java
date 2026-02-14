package interview.lld.interviewmain.cachettl;

/*
Problem: Storing key-value pairs where each pair has an expiration time.
    - CacheEntry : holds key, value, expirationTime
    - EntryStorage : ConcurrentHashMap -> CacheEntry
    - CleanUp : Lazy cleanup / Eager cleanup
Thread Safe - Cache with TTL : In-Memory Storage
- Design a thread-safe in-memory cache that supports get/put operations with per-entry TTL,
  where expired entries are never returned and eventually cleaned up.
- Per-entry TTL : Each entry has its own expiration time, not a global TTL for the entire cache.
- Memory cleanup : Expired entries must eventually be removed to prevent memory leaks.


Operations:
    - get
    - put
    - remove perEntry TTL
    - Thread Safety
    - Memory CleanUp

 */


import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Approach 3: Combine ConcurrentHashMap for thread-safe access with a scheduled background thread that periodically removes expired entries.
// If Memory concern then go with Approach-3 else Approach-2 is fine.
/*
How it works
    - User operations use a thread-safe map just like Approach 2
    - A scheduled task runs cleanup every N seconds
    - Cleanup iterates through entries and removes expired ones
    - Use atomic check-and-remove to avoid the put-cleanup race

 */


public class ThreadSafeCacheTTL<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache;
    private final ScheduledExecutorService cleanupExecutor;

    public ThreadSafeCacheTTL() {
        this(0);  // No background cleanup by default
    }

    public ThreadSafeCacheTTL(long cleanupIntervalMs) {
        this.cache = new ConcurrentHashMap<>();

        if (cleanupIntervalMs > 0) {
            // Background cleanup thread
            this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "cache-cleanup");
                t.setDaemon(true);  // Don't prevent JVM shutdown
                return t;
            });
            cleanupExecutor.scheduleAtFixedRate(
                    this::cleanupExpiredEntries,
                    cleanupIntervalMs,
                    cleanupIntervalMs,
                    TimeUnit.MILLISECONDS
            );
        } else {
            this.cleanupExecutor = null;
        }
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry == null) {
            return null;
        }

        if (entry.isExpired()) {
            // Atomic removal: only remove if entry hasn't been replaced
            // This prevents the put-cleanup race
            cache.remove(key, entry);
            return null;
        }

        return entry.getValue();
    }

    public void put(K key, V value, long ttlMs) {
        cache.put(key, new CacheEntry<>(value, ttlMs));
    }

    public boolean remove(K key) {
        return cache.remove(key) != null;
    }

    public int size() {
        // Note: this includes potentially expired entries
        return cache.size();
    }

    public int activeSize() {
        // Count only non-expired entries
        return (int) cache.values().stream()
                .filter(entry -> !entry.isExpired())
                .count();
    }

    private void cleanupExpiredEntries() {
        for (K key : cache.keySet()) {
            // Use compute for atomic check-and-remove
            // This prevents racing with put operations
            cache.compute(key, (k, entry) -> {
                if (entry != null && entry.isExpired()) {
                    return null;  // Remove by returning null
                }
                return entry;  // Keep non-expired or already-null
            });
        }
    }

    public void shutdown() {
        if (cleanupExecutor != null) {
            cleanupExecutor.shutdown();
            try {
                cleanupExecutor.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        cache.clear();
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



package interview.corerjava.multithreading.datastructure;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/*
put(key, value)	Insert or update a key-value pair	O(1) average
get(key)	Retrieve value for a key	O(1) average
remove(key)	Delete a key-value pair	O(1) average
putIfAbsent(key, value)	Insert only if key doesn't exist	O(1) average
computeIfAbsent(key, func)	Compute and insert if key missing	O(1) average
size()	Return number of entries	O(1) or O(segments)

Multiple threads can call get() simultaneously without blocking each other
Multiple threads can call put() on different keys simultaneously
Operations on the same key must be serialized to prevent lost updates
Compound operations like putIfAbsent() must be atomic
No thread should see partially constructed entries
Iterators should be weakly consistent (no ConcurrentModificationException)

Core concept:
    - Bucket Array
    - Hash Function
    - Collision Handling
    - Load Factor

 */
//1. Let's first start with SimpleHashMap : Single threaded
class SimpleHashMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16; // Always power of 2
    private static final float LOAD_FACTOR = 0.75f;

    private Node<K, V>[] table;
    private int size;

    static class Node<K, V> {
        final K key;
        V value;
        Node<K, V> next;
        final int hash;


        Node (int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    @SuppressWarnings("unchecked")
    public SimpleHashMap() {
        table = new Node[DEFAULT_CAPACITY];
    }

    public V put (K key, V value) {
        int hash = hash(key);
        int index = hash & (table.length - 1);

        // Check if key exists
        for (Node<K, V> node = table[index]; node!=null; node = node.next) {
            if (node.hash == hash && key.equals(node.key)) {
                V oldValue = node.value;
                node.value = value;
                return oldValue;
            }
        }

        // else Add a new at head of chain
        table[index] = new Node<>(hash, key, value, table[index]);
        if (++size > table.length * LOAD_FACTOR) {
            resize();
        }
        return null;

    }

    public V get (K key) {
        int hash = hash(key);
        int index = hash & (table.length - 1);

        for (Node<K, V> node = table[index]; node != null; node=node.next) {
            if (node.hash == hash && key.equals(node.key)) {
                return node.value;
            }
        }
        return null;
    }


    private int hash(K key) {
        int h = key.hashCode(); // h = 10110110 01011100 11001010 11110000   (32 bits)
        return h ^ (h >>> 16); // It shifts the number 16 bits to the right and fills left with zeros. 00000000 00000000 10110110 01011100
        // return Math.abs(31 * h + 17);
    }

    private void resize() { /* Double capacity and rehash */ }

    static void main() {
        SimpleHashMap<Integer, String> simpleHashMap = new SimpleHashMap<>();
        simpleHashMap.put(1, "Deepak");
        simpleHashMap.put(6, "Ravi");
        simpleHashMap.put(4, "Ansh");

        System.out.println(simpleHashMap.get(4));

    }
}

//Handling Concurrency
// Approach 1: Coarse-Grained Locking : Warapp with single lock on operation and operations will be serialised.
class SynchronizedHashMap<K, V> {
    private final HashMap<K, V> map = new HashMap<>();
    private  final ReentrantLock lock = new ReentrantLock();

    public V put (K key, V value) {
        lock.lock();
        try {
           return map.put(key, value); // returning the same contract as HashMap
        } finally {
            lock.unlock();
        }
    }

    public V get (K key) {
        lock.lock();
        try {
            return map.get(key);
        } finally {
            lock.unlock();
        }
    }
}




// Approach 2:  Fine-Grained Locking (Lock Striping) : operations on different buckets can proceed in parallel (used in ConcurrentHashMap)
// Strategy: Segment-Based Locking : Divide the hash table into segments, each with its own lock.
// segment = hash(key) % NUM_SEGMENTS


class StripedConcurrentHashMap<K, V> {
    private static final int DEFAULT_SEGMENTS = 16;
    private static final int DEFAULT_CAPACITY_PER_SEGMENT = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private final Segment<K, V>[] segments;

    @SuppressWarnings("unchecked")
    public StripedConcurrentHashMap() {
        this(DEFAULT_SEGMENTS);
    }

    @SuppressWarnings("unchecked")
    public StripedConcurrentHashMap(int numSegments) {
        segments = new Segment[numSegments];
        for (int i = 0; i < numSegments; i++) {
            segments[i] = new Segment<>(DEFAULT_CAPACITY_PER_SEGMENT);
        }
    }

    private int hash(K key) {
        int h = key.hashCode();
        // Spread bits to reduce collisions in segment selection
        h ^= (h >>> 20) ^ (h >>> 12) ^ (h >>> 7) ^ (h >>> 4);
        return h;
    }

    private Segment<K, V> segmentFor(K key) {
        int hash = hash(key);
        int segmentIndex = (hash >>> 28) & (segments.length - 1);
        return segments[segmentIndex];
    }

    public V put(K key, V value) {
        return segmentFor(key).put(key, hash(key), value);
    }

    public V get(K key) {
        return segmentFor(key).get(key, hash(key));
    }

    public V remove(K key) {
        return segmentFor(key).remove(key, hash(key));
    }

    public V putIfAbsent(K key, V value) {
        return segmentFor(key).putIfAbsent(key, hash(key), value);
    }

    public int size() {
        // Must lock all segments for accurate count
        int total = 0;

        // Lock all segments in order to prevent deadlock
        for (Segment<K, V> segment : segments) {
            segment.lock.lock();
        }

        try {
            for (Segment<K, V> segment : segments) {
                total += segment.count;
            }
        } finally {
            // Unlock in reverse order (not strictly necessary but good practice)
            for (int i = segments.length - 1; i >= 0; i--) {
                segments[i].lock.unlock();
            }
        }

        return total;
    }

    // Inner Segment class - essentially a small synchronized HashMap
    private static class Segment<K, V> {
        final ReentrantLock lock = new ReentrantLock();
        Node<K, V>[] table;
        int count;

        @SuppressWarnings("unchecked")
        Segment(int capacity) {
            table = new Node[capacity];
            count = 0;
        }

        V put(K key, int hash, V value) {
            lock.lock();
            try {
                int index = hash & (table.length - 1);

                for (Node<K, V> node = table[index]; node != null; node = node.next) {
                    if (node.hash == hash && key.equals(node.key)) {
                        V oldValue = node.value;
                        node.value = value;
                        return oldValue;
                    }
                }

                // Add new node
                table[index] = new Node<>(hash, key, value, table[index]);
                count++;

                // Resize if needed (within segment)
                if (count > table.length * LOAD_FACTOR) {
                    resize();
                }

                return null;
            } finally {
                lock.unlock();
            }
        }

        V get(K key, int hash) {
            lock.lock();
            try {
                int index = hash & (table.length - 1);
                for (Node<K, V> node = table[index]; node != null; node = node.next) {
                    if (node.hash == hash && key.equals(node.key)) {
                        return node.value;
                    }
                }
                return null;
            } finally {
                lock.unlock();
            }
        }

        V remove(K key, int hash) {
            lock.lock();
            try {
                int index = hash & (table.length - 1);
                Node<K, V> prev = null;

                for (Node<K, V> node = table[index]; node != null; prev = node, node = node.next) {
                    if (node.hash == hash && key.equals(node.key)) {
                        if (prev == null) {
                            table[index] = node.next;
                        } else {
                            prev.next = node.next;
                        }
                        count--;
                        return node.value;
                    }
                }
                return null;
            } finally {
                lock.unlock();
            }
        }

        V putIfAbsent(K key, int hash, V value) {
            lock.lock();
            try {
                int index = hash & (table.length - 1);

                for (Node<K, V> node = table[index]; node != null; node = node.next) {
                    if (node.hash == hash && key.equals(node.key)) {
                        return node.value;  // Key exists, return current value
                    }
                }

                // Key doesn't exist, add it
                table[index] = new Node<>(hash, key, value, table[index]);
                count++;

                if (count > table.length * LOAD_FACTOR) {
                    resize();
                }

                return null;
            } finally {
                lock.unlock();
            }
        }

        @SuppressWarnings("unchecked")
        private void resize() {
            Node<K, V>[] oldTable = table;
            int newCapacity = oldTable.length * 2;
            Node<K, V>[] newTable = new Node[newCapacity];

            for (Node<K, V> head : oldTable) {
                for (Node<K, V> node = head; node != null; ) {
                    Node<K, V> next = node.next;
                    int newIndex = node.hash & (newCapacity - 1);
                    node.next = newTable[newIndex];
                    newTable[newIndex] = node;
                    node = next;
                }
            }

            table = newTable;
        }
    }

    private static class Node<K, V> {
        final int hash;
        final K key;
        volatile V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}




/**
 * Thread-safe HashMap using lock striping (segment-based locking).
 *
 * Thread Safety: Operations on different segments proceed in parallel.
 *                Operations on the same segment are serialized.
 * Performance: Up to 16x parallelism over synchronized HashMap.
 *
 * @param <K> Key type
 * @param <V> Value type
 */
/*
Approach 3: CAS-Based
Java 8 redesigned ConcurrentHashMap to eliminate segment locks for most operations. The key innovations:
 */
public class ConcurrentHashMap<K, V> {
    private static final int DEFAULT_SEGMENTS = 16;
    private static final int DEFAULT_CAPACITY_PER_SEGMENT = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private final Segment<K, V>[] segments;
    private final AtomicInteger totalSize = new AtomicInteger(0);

    @SuppressWarnings("unchecked")
    public ConcurrentHashMap() {
        this(DEFAULT_SEGMENTS, DEFAULT_CAPACITY_PER_SEGMENT);
    }

    @SuppressWarnings("unchecked")
    public ConcurrentHashMap(int numSegments, int capacityPerSegment) {
        // Ensure power of 2 for fast modulo
        numSegments = tableSizeFor(numSegments);
        segments = new Segment[numSegments];
        for (int i = 0; i < numSegments; i++) {
            segments[i] = new Segment<>(capacityPerSegment);
        }
    }

    // Hash spreading function
    private int spread(int h) {
        return (h ^ (h >>> 16)) & 0x7fffffff;
    }

    // Get segment for a key
    private Segment<K, V> segmentFor(K key) {
        int hash = spread(key.hashCode());
        int segmentIndex = hash & (segments.length - 1);
        return segments[segmentIndex];
    }

    /**
     * Associates the specified value with the specified key.
     * @return previous value, or null if no previous mapping
     */
    public V put(K key, V value) {
        if (key == null || value == null) throw new NullPointerException();
        Segment<K, V> segment = segmentFor(key);
        V oldValue = segment.put(key, spread(key.hashCode()), value);
        if (oldValue == null) {
            totalSize.incrementAndGet();
        }
        return oldValue;
    }

    /**
     * Returns the value for the specified key, or null if not found.
     */
    public V get(K key) {
        if (key == null) throw new NullPointerException();
        return segmentFor(key).get(key, spread(key.hashCode()));
    }

    /**
     * Removes the mapping for the specified key.
     * @return previous value, or null if no mapping existed
     */
    public V remove(K key) {
        if (key == null) throw new NullPointerException();
        Segment<K, V> segment = segmentFor(key);
        V oldValue = segment.remove(key, spread(key.hashCode()));
        if (oldValue != null) {
            totalSize.decrementAndGet();
        }
        return oldValue;
    }

    /**
     * Inserts the value only if the key is not already present.
     * @return existing value if present, null if inserted
     */
    public V putIfAbsent(K key, V value) {
        if (key == null || value == null) throw new NullPointerException();
        Segment<K, V> segment = segmentFor(key);
        V result = segment.putIfAbsent(key, spread(key.hashCode()), value);
        if (result == null) {
            totalSize.incrementAndGet();
        }
        return result;
    }

    /**
     * Computes the value if the key is not present.
     * The mapping function is called at most once.
     */
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        if (key == null || mappingFunction == null) throw new NullPointerException();
        Segment<K, V> segment = segmentFor(key);
        return segment.computeIfAbsent(key, spread(key.hashCode()), mappingFunction, totalSize);
    }

    /**
     * Returns true if the map contains the specified key.
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Returns the approximate number of entries.
     * May not be accurate during concurrent modifications.
     */
    public int size() {
        return totalSize.get();
    }

    /**
     * Returns true if the map contains no entries.
     */
    public boolean isEmpty() {
        return totalSize.get() == 0;
    }

    /**
     * Removes all entries from the map.
     */
    public void clear() {
        for (Segment<K, V> segment : segments) {
            segment.clear();
        }
        totalSize.set(0);
    }

    // Compute next power of 2 >= n
    private static int tableSizeFor(int n) {
        n = n - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= (1 << 30)) ? (1 << 30) : n + 1;
    }

    // ==================== Inner Classes ====================

    private static class Segment<K, V> {
        private final ReentrantLock lock = new ReentrantLock();
        private Node<K, V>[] table;
        private int count;

        @SuppressWarnings("unchecked")
        Segment(int capacity) {
            table = new Node[capacity];
            count = 0;
        }

        V put(K key, int hash, V value) {
            lock.lock();
            try {
                int index = hash & (table.length - 1);

                for (Node<K, V> node = table[index]; node != null; node = node.next) {
                    if (node.hash == hash && key.equals(node.key)) {
                        V oldValue = node.value;
                        node.value = value;
                        return oldValue;
                    }
                }

                table[index] = new Node<>(hash, key, value, table[index]);
                count++;

                if (count > table.length * LOAD_FACTOR) {
                    resize();
                }

                return null;
            } finally {
                lock.unlock();
            }
        }

        V get(K key, int hash) {
            lock.lock();
            try {
                int index = hash & (table.length - 1);
                for (Node<K, V> node = table[index]; node != null; node = node.next) {
                    if (node.hash == hash && key.equals(node.key)) {
                        return node.value;
                    }
                }
                return null;
            } finally {
                lock.unlock();
            }
        }

        V remove(K key, int hash) {
            lock.lock();
            try {
                int index = hash & (table.length - 1);
                Node<K, V> prev = null;

                for (Node<K, V> node = table[index]; node != null; prev = node, node = node.next) {
                    if (node.hash == hash && key.equals(node.key)) {
                        if (prev == null) {
                            table[index] = node.next;
                        } else {
                            prev.next = node.next;
                        }
                        count--;
                        return node.value;
                    }
                }
                return null;
            } finally {
                lock.unlock();
            }
        }

        V putIfAbsent(K key, int hash, V value) {
            lock.lock();
            try {
                int index = hash & (table.length - 1);

                for (Node<K, V> node = table[index]; node != null; node = node.next) {
                    if (node.hash == hash && key.equals(node.key)) {
                        return node.value;
                    }
                }

                table[index] = new Node<>(hash, key, value, table[index]);
                count++;

                if (count > table.length * LOAD_FACTOR) {
                    resize();
                }

                return null;
            } finally {
                lock.unlock();
            }
        }

        V computeIfAbsent(K key, int hash, Function<? super K, ? extends V> mapper,
                          AtomicInteger totalSize) {
            lock.lock();
            try {
                int index = hash & (table.length - 1);

                for (Node<K, V> node = table[index]; node != null; node = node.next) {
                    if (node.hash == hash && key.equals(node.key)) {
                        return node.value;
                    }
                }

                V value = mapper.apply(key);
                if (value != null) {
                    table[index] = new Node<>(hash, key, value, table[index]);
                    count++;
                    totalSize.incrementAndGet();

                    if (count > table.length * LOAD_FACTOR) {
                        resize();
                    }
                }

                return value;
            } finally {
                lock.unlock();
            }
        }

        void clear() {
            lock.lock();
            try {
                for (int i = 0; i < table.length; i++) {
                    table[i] = null;
                }
                count = 0;
            } finally {
                lock.unlock();
            }
        }

        @SuppressWarnings("unchecked")
        private void resize() {
            Node<K, V>[] oldTable = table;
            int newCapacity = oldTable.length * 2;
            Node<K, V>[] newTable = new Node[newCapacity];

            for (Node<K, V> head : oldTable) {
                while (head != null) {
                    Node<K, V> next = head.next;
                    int newIndex = head.hash & (newCapacity - 1);
                    head.next = newTable[newIndex];
                    newTable[newIndex] = head;
                    head = next;
                }
            }

            table = newTable;
        }
    }

    private static class Node<K, V> {
        final int hash;
        final K key;
        volatile V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
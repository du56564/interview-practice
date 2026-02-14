package interview.lld.interviewmain;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

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

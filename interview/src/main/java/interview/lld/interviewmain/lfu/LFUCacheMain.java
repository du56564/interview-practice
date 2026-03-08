package interview.lld.interviewmain.lfu;


import java.util.HashMap;
import java.util.Map;

class Node<K, V> {
    K key;
    V value;
    int freq = 1;
    Node<K, V> prev;
    Node<K, V> next;

    Node(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

class DoublyLinkedList<K, V> {
    private Node<K, V> head;
    private Node<K, V> tail;
    public DoublyLinkedList() {
        /*
        Q. Why dummy head & tail?
        -Remove null checks
	    -Simplify edge cases (empty list, 1 element)
         */
        head = new Node<>(null, null);
        tail = new Node<>(null, null);
        head.next = tail;
        tail.prev = head;
    }
    //Adds a node right after the head (most recently used position).
    public void addFirst(Node<K, V> node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    //Detaches a node from its current position.
    public void remove(Node<K, V> node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    //Moves an existing node to the front, marking it as recently used.
    public void moveToFront(Node<K, V> node) {
        remove(node);
        addFirst(node);
    }

    //Removes and returns the LRU node (just before the tail).
    public Node<K, V> removeLast() {
        if (tail.prev == head) return null;
        Node<K, V> last = tail.prev;
        remove(last);
        return last;
    }
}



class LFUCache<K, V> {

    private final int capacity;
    private int minFreq = 0;

    // key → node
    private final Map<K, Node<K, V>> nodeMap;

    // freq → DLL
    private final Map<Integer, DoublyLinkedList<K, V>> freqMap;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.nodeMap = new HashMap<>();
        this.freqMap = new HashMap<>();
    }

    public synchronized V get(K key) {
        if (!nodeMap.containsKey(key)) return null;

        Node<K, V> node = nodeMap.get(key);
        increaseFreq(node);
        return node.value;
    }

    public synchronized void put(K key, V value) {
        if (capacity == 0) return;

        if (nodeMap.containsKey(key)) {
            Node<K, V> node = nodeMap.get(key);
            node.value = value;
            increaseFreq(node);
            return;
        }

        // Eviction
        if (nodeMap.size() == capacity) {
            DoublyLinkedList<K, V> minList = freqMap.get(minFreq);
            Node<K, V> evict = minList.removeLast();
            nodeMap.remove(evict.key);
        }

        // New node
        Node<K, V> newNode = new Node<>(key, value);
        nodeMap.put(key, newNode);

        freqMap.computeIfAbsent(1, f -> new DoublyLinkedList<>())
                .addFirst(newNode);

        minFreq = 1; // reset because new node has freq = 1
    }

    private void increaseFreq(Node<K, V> node) {
        int oldFreq = node.freq;
        DoublyLinkedList<K, V> oldList = freqMap.get(oldFreq);
        oldList.remove(node);

        // update minFreq
        if (oldFreq == minFreq && oldList.removeLast() == null) {
            minFreq++;
        }

        node.freq++;
        freqMap.computeIfAbsent(node.freq, f -> new DoublyLinkedList<>())
                .addFirst(node);
    }
}

public class LFUCacheMain {

    static void main() {
        LFUCache<String, Integer> cache = new LFUCache<>(2);
        cache.put("a", 1);
        cache.put("b", 2);
        cache.put("c", 3);
        cache.put("d", 1);
        System.out.println(cache.get("c"));
        cache.put("d", 5);
        System.out.println(cache.get("c"));
        System.out.println(cache.get("c"));
        System.out.println(cache.get("c"));
        System.out.println(cache.get("c"));
        cache.put("a", 1);
    }

}

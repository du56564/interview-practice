package interview.lld.interviewmain.mfu;


import java.util.HashMap;
import java.util.Map;
/*
Example:
MFUCache cache = new MFUCache(2);

cache.put(1, 10);
cache.put(2, 20);

cache.get(1);   // freq(1)=2, freq(2)=1

cache.put(3, 30);  // capacity full → evict MOST FREQUENT (key=1)

cache.get(1);  // -1 (evicted)
cache.get(2);  // 20
cache.get(3);  // 30

[(2,20,1),(1,2,1))]
get(1) = 20 : [(1,2,1)), (2,20,1)]
cache.put(3,30) : [(3,30,1), (2,20,2))]
* */
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



class MFUCache<K, V> {

    private final int capacity;
    private int maxFreq = 0;

    // key → node
    private final Map<K, Node<K, V>> nodeMap;

    // freq → DLL of nodes
    private final Map<Integer, DoublyLinkedList<K, V>> freqMap;

    public MFUCache(int capacity) {
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
            DoublyLinkedList<K, V> maxFreqList = freqMap.get(maxFreq);
            Node<K, V> evict = maxFreqList.removeLast();
            nodeMap.remove(evict.key);
        }

        // Insert new node
        Node<K, V> newNode = new Node<>(key, value);
        nodeMap.put(key, newNode);

        freqMap.computeIfAbsent(1, f -> new DoublyLinkedList<>()) //Logic to consider : init (1, dll)
                .addFirst(newNode);

        maxFreq = Math.max(maxFreq, 1);
    }

    private void increaseFreq(Node<K, V> node) {
        int oldFreq = node.freq;
        DoublyLinkedList<K, V> oldList = freqMap.get(oldFreq);
        oldList.remove(node);

        node.freq++;
        freqMap.computeIfAbsent(node.freq, f -> new DoublyLinkedList<>())
                .addFirst(node);
        maxFreq = Math.max(maxFreq, node.freq);
    }
}

public class MFUCacheMain {
    static void main() {
        MFUCache<String, Integer> cache = new MFUCache<>(2);
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

package interview.lld.lru;

// when buffer is full then evict least recently used item.
// LRU - key, value pair to store and retrieve
/*

put(1, A)     // cache: {1:A}
put(2, B)     // cache: {1:A, 2:B}
put(3, C)     // cache: {1:A, 2:B, 3:C}
get(1)        // access key 1 → makes it most recently used
put(4, D)     // key 2 is least recently used → evict it

get(1) : remove item key,value from map and linkedlist then add at last
put(4) :

Move a recently accessed item to the front (marking it as Most Recently Used, or MRU)
Remove the least recently used item from the back when the cache exceeds its capacity
Insert new items to the front (they're considered most recently used)
Perform all of these operations in O(1) time

Data structure: HashMap<K, V> and DoublyLinkedList
Hash Map: Provides O(1) lookup. Instead of storing the value directly,
                                it will store a pointer/reference to the node in our DLL.
Doubly Linked List: Maintains the usage order.
                    The head of the list will always be the Most Recently Used (MRU) item,
                    and the tail will be the Least Recently Used (LRU) item.

//Design Pattern : Facade
Class:
    Node - Represent data of DLL: K,V,prev,next
    LRUCache - (key, DLL)
    DoublyLinkedList - MRU at front and LRU at last
                     - head, tail, moveToFron, removeLast, remove, addFirst
    Main

Attributes:
    capacity: int
    map: Map<K, Node<K, V>>
    list: DoublyLinkedList<K, V>
Method:
    put(K, V) : void
    get(K) : V

Composition ("has-a")
    LRUCache has-a DoublyLinkedList
    LRUCache has-a Map<K, Node<K, V>>
    DoublyLinkedList has-a Node<K, V> for both head and tail
Association ("uses-a")
    LRUCache uses Node to store cache entries and manipulate ordering
    DoublyLinkedList uses Node to manage the list structure

 */

import java.util.HashMap;
import java.util.Map;

class Node<K, V> {
    K key;
    V value;
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

class LRUCache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> map;
    private final DoublyLinkedList<K, V> dll;

    public LRUCache (int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();
        dll = new DoublyLinkedList<>();
    }
    public synchronized V get(K key) {
        if (!map.containsKey(key)) {
            return null;
        }
        Node<K, V> node = map.get(key);
        dll.moveToFront(node);
        return node.value;
    }

    public synchronized void put(K key, V value) {
        //if key present then move node to front and update cache Value
        if (map.containsKey(key)) {
            Node<K, V> node = map.get(key);
            node.value = value;
            dll.moveToFront(node);
        } else {
            //if capacity is full
            if (map.size() == capacity) {
                Node<K, V> lru = dll.removeLast();
                if (lru!=null)   map.remove(lru.key);
            }
            //fresh node
            Node<K, V> newNode = new Node<>(key, value);
            dll.addFirst(newNode);
            map.put(key, newNode);
        }
    }

}

public class LRUCacheMain {
    static void main() {
        LRUCache<String, Integer> cache = new LRUCache<>(3);
        cache.put("a", 1); //recent used
        cache.put("b", 2); // will get remove if buffer increases
        cache.put("c", 3); // recent added

        // Accessing 'a' makes it the most recently used
        System.out.println(cache.get("a")); // 1

        // Adding 'd' will cause 'b' (the current LRU item) to be evicted
        cache.put("d", 4);

        // Trying to get 'b' should now return null
        System.out.println(cache.get("b")); // null
    }
}

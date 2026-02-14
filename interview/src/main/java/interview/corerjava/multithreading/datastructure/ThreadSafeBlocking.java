package interview.corerjava.multithreading.datastructure;

/*
ArrayBlockingQueue = Fixed-size + Single lock + Less memory
LinkedBlockingQueue = Linked nodes + Dual lock + Better concurrency

 */
class CircularQueue<E> {
    private final Object[] items;
    private int head;     // Index of next element to dequeue
    private int tail;     // Index of next empty slot for enqueue
    private int count;    // Current number of elements

    public CircularQueue(int capacity) {
        items = new Object[capacity];
        head = 0;
        tail = 0;
        count = 0;
    }

    public boolean offer(E item) {
        if (items.length == count) {
            return false;
        }
        items[tail] = item;
        tail = (tail+1) % items.length; // Why +1? Because after inserting an element at the current tail index, we need to move the tail pointer to the next position in the circular array.
        count++;
        return true;
    }

    @SuppressWarnings("unchecked")
    public E poll() {
        if (count == 0) {
            return null;
        }
        E item = (E) items[head];
        items[head] = null;
        head = (head+1) % items.length;
        count--;
        return item;
    }

    @SuppressWarnings("unchecked")
    public E peek() {
        if (count == 0) {
            return null;
        }
        return (E) items[head];
    }

    public int size() {
        return count;
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == items.length;
    }

    static void main() {
        CircularQueue<String> queue = new CircularQueue<>(10);
        queue.offer("A");
        queue.offer("B");
        queue.offer("C");

        System.out.println(queue.peek());


    }
}

public class ThreadSafeBlocking {

}

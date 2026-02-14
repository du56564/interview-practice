package interview.lld.interviewmain;

import java.util.LinkedList;
import java.util.Queue;

// Core implementation
class BoundedBuffer<T> {
    private final Queue<T> queue;
    private final int capacity;

    public BoundedBuffer(int bufferSize) {
        this.queue = new LinkedList<>();
        this.capacity = bufferSize;
    }

    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() >= capacity) {
            wait();
        }
        queue.offer(item);
        notifyAll();
    }

    public synchronized T take () throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.remove();
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }
}
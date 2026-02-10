package interview.lld.designpattern;


/*
Producers add work to buffer
Consumer take from them
Decouple production of data from its processing
Asynchronous via a queue/buffer
Producer doesn’t know consumers at all


*/

import java.util.LinkedList;
import java.util.Queue;

class BoundedBuffer<T> {

    private final Queue<T> queue;
    private final int capacity;

    public BoundedBuffer(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
    }
    public synchronized void put(T item) throws InterruptedException {
        // Wait while buffer is full
        while (queue.size() == capacity) {
            wait();
        }
        queue.add(item);

        // Notify consumers that an item is available
        notifyAll();
    }

    public synchronized T  take() throws InterruptedException {
        // Wait while buffer is empty
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.poll();

        // Notify producers that space is available
        notifyAll();
        return item;
    }
}

public class ProducerConsumer {
    /*
    BoundedBuffer<Task> buffer = new BoundedBuffer<>(100);

    // Producer thread
    void produce() {
        while (running) {
            Task task = generateTask();
            buffer.put(task);  // Blocks if buffer is full
        }
    }

    // Consumer thread
    void consume() {
        while (running) {
            Task task = buffer.take();  // Blocks if buffer is empty
            process(task);
        }
    }

     */
    static void main() {
        ProducerConsumer producerConsumer = new ProducerConsumer();
        //producerConsumer.produce();
        //ProducerConsumer.consume();

    }


}

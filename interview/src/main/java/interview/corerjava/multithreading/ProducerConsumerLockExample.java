package interview.corerjava.multithreading;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerLockExample {
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private final int[] buffer = new int[5];
    private int count = 0;

    public void produce(int value) throws InterruptedException {
        lock.lock();
        try {
            while (count == buffer.length) {
                notEmpty.await(); // Wait if buffer is full
            }
            buffer[count++] = value;
            System.out.println("Produced: " + value);
            notEmpty.signal(); // Notify consumers
        } finally {
            lock.unlock();
        }
    }

    public int consume() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await(); // Wait if buffer is empty
            }
            int value = buffer[--count];
            System.out.println("Consumed: " + value);
            notEmpty.signal(); // Notify producers
            return value;
        } finally {
            lock.unlock();
        }
    }
}
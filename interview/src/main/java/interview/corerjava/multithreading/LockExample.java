package interview.corerjava.multithreading;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//Exclusive Locks:  allow only one thread to access a resource at a time.
public class LockExample {
    private final Lock lock = new ReentrantLock();
    private int sharedResource = 0;

    public void increment() {
        lock.lock(); // Acquire the lock
        try {
            sharedResource++;
            System.out.println("Resource incremented: " + sharedResource);
        } finally {
            lock.unlock(); // Always unlock in a finally block
        }
    }
}
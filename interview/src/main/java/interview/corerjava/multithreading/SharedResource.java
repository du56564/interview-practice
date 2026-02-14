package interview.corerjava.multithreading;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// Shared locks allow multiple threads to read a resource simultaneously but prevent any thread from modifying it.
public class SharedResource {
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private int sharedData = 0;

    public void read() {
        rwLock.readLock().lock();
        try {
            System.out.println("Reading data: " + sharedData);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    public void write(int value) {
        rwLock.writeLock().lock();
        try {
            sharedData = value;
            System.out.println("Writing data: " + sharedData);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    // tryLock
    public void tryLockExample() {
        final ReentrantLock lock = new ReentrantLock();
        if (lock.tryLock()) {
            try {
                // Perform actions if lock is acquired
                System.out.println("Lock acquired");
            } finally {
                lock.unlock();
            }
        } else {
            System.out.println("Lock not acquired, performing other tasks");
        }
    }
}
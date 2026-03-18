package interview.lld.interviewmain;

import java.util.ArrayList;
import java.util.List;

/*
 Implemented a thread pool using previously made bounded queue. These methods were required to implement:
    1. submit()
    2. execute()
    3. shutdown()
    4. shutdownNow()

    Client Threads
          |
          v
    submit() / execute()
          |
          v
    BoundedQueue<Runnable>
          |
          v
    Worker Threads (fixed pool)
          |
          v
    task.run()
 */

class SimpleThreadPool {

    private BoundedQueue<Runnable> taskQueue;
    private List<Worker> workers = new ArrayList<>();
    private volatile boolean isShutdown = false;

    public SimpleThreadPool(int poolSize, int queueCapacity) {
        taskQueue = new BoundedQueue<>(queueCapacity);
        for (int i = 0; i < poolSize; i++) {
            Worker worker = new Worker();
            Thread thread = new Thread(worker);
            workers.add(worker); // just used for shutdown()
            thread.start();
        }
    }

    // Similar to Executor.execute()
    public void execute(Runnable task) throws InterruptedException {
        if (isShutdown) {
            throw new IllegalStateException("ThreadPool is shutdown");
        }
        taskQueue.put(task);
    }

    // Similar to ExecutorService.submit()
    public void submit(Runnable task) throws InterruptedException {
        execute(task);
    }

    // Graceful shutdown
    public void shutdown() {
        isShutdown = true;
    }

    // Force shutdown
    public List<Runnable> shutdownNow() {
        isShutdown = true;
        for (Worker worker : workers) {

            new Thread(worker).interrupt();
        }
        return new ArrayList<>();
    }

    // Worker Thread
    private class Worker implements Runnable {

        @Override
        public void run() {

            while (!isShutdown || taskQueue.size() > 0) { //this alives thread
                try {

                    Runnable task = taskQueue.take();
                    task.run();

                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }



    static void main(String[] args) throws Exception {

        SimpleThreadPool pool = new SimpleThreadPool(3, 10);

        for (int i = 0; i < 200; i++) {

            int taskId = i;
            Runnable runnable = () -> {
                System.out.println(
                        Thread.currentThread().getName() +
                                " executing task " + taskId
                );
            };
            pool.submit(runnable);
        }

        pool.shutdown();
    }
}
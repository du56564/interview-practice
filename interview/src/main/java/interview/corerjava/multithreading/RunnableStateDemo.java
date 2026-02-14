package interview.corerjava.multithreading;

public class RunnableStateDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            // This thread is RUNNABLE while executing
            // (Java combines RUNNABLE and RUNNING)
            for (int i = 0; i < 1_000_000; i++) {
                // Busy work

                System.out.println( Math.sqrt(i));
            }
        });

        thread.start();  // Transitions from NEW to RUNNABLE

        // Give it a moment to start
        Thread.sleep(10);
        System.out.println("State: " + thread.getState());  // RUNNABLE

        thread.join();
    }
}
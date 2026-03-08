package interview.lld.interviewmain;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// Using library
public class ProducerConsumer {
    static void main() throws InterruptedException {
        //BlockingQueue<String> buffer = new LinkedBlockingQueue<>(); /// in house library
        BoundedQueue<String> buffer = new BoundedQueue<>(10);
        // consumer
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String item = buffer.take();
                    if (item.equals("POISON")) break;
                    System.out.println("Consumed:"+ item);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();


        // Produce items
        for (int i = 0; i < 20; i++) {
            buffer.put("Item-" + i);  // Blocks if full
            System.out.println("Produced: Item-" + i);
        }

        // Send poison pill to signal shutdown
        buffer.put("POISON");
        consumer.join();
    }
}

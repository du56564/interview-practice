package interview.lld.pubsubsystem.observer;

import interview.lld.pubsubsystem.entity.Message;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;

public class TopicSubject implements Topic {
    private final String name;
    private final Set<Subscriber> subscribers;
    private final ExecutorService deliveryExecutor;

    public TopicSubject(String name, ExecutorService deliveryExecutor) {
        this.name = name;
        this.deliveryExecutor = deliveryExecutor;
        this.subscribers = new CopyOnWriteArraySet<>(); // Thread-safe set
    }

    @Override
    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }

    @Override
    public void removeSubscriber(Subscriber subscriber) {
        subscribers.remove(subscriber);
    }

    @Override
    public void broadcast(Message message) {
        for (Subscriber subscriber : subscribers) {
            deliveryExecutor.submit(() -> { // Asynchronous delivery of message
                try {
                    subscriber.onMessage(message);
                } catch (Exception e) {
                    System.err.println("Error delivering message to subscriber " + subscriber.getId() + ": " + e.getMessage());
                }
            });
        }
    }
}

package interview.lld.pubsubsystem.observer;

import interview.lld.pubsubsystem.entity.Message;

// like subject
public interface Topic {
    void addSubscriber (Subscriber subscriber);
    void removeSubscriber (Subscriber subscriber);
    void broadcast (Message message);
}

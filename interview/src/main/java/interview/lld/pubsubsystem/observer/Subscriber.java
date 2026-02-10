package interview.lld.pubsubsystem.observer;

import interview.lld.pubsubsystem.entity.Message;

// Observer
public interface Subscriber {
    String getId();
    void onMessage (Message message);
}

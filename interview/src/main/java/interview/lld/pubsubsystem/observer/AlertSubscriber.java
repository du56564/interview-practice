package interview.lld.pubsubsystem.observer;

import interview.lld.pubsubsystem.entity.Message;

public class AlertSubscriber implements Subscriber {
    private final String id;

    public AlertSubscriber (String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void onMessage(Message message) {
        System.out.printf("!!! [ALERT - %s] : '%s' !!!%n", id, message.getPayload());
    }
}

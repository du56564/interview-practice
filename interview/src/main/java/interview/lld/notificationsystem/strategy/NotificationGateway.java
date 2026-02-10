package interview.lld.notificationsystem.strategy;

import interview.lld.notificationsystem.entities.Notification;

public interface NotificationGateway {
    void send (Notification notification) throws Exception;
}

package interview.lld.notificationsystem.strategy;

import interview.lld.notificationsystem.entities.Notification;

public class PushGateway implements NotificationGateway {
    @Override
    public void send(Notification notification) throws Exception {
        String token = notification.getRecipient().getPushToken()
                .orElseThrow(() -> new IllegalArgumentException("Push token is required for PUSH notification."));
        System.out.println("--- Sending PUSH Notification ---");
        System.out.println("To Device Token: " + token);
        System.out.println("Title: " + notification.getSubject()); // Re-using subject for title
        System.out.println("Body: " + notification.getMessage());
        System.out.println("---------------------------------\n");
    }
}
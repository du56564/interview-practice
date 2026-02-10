package interview.lld.notificationsystem.strategy;

import interview.lld.notificationsystem.entities.Notification;

public class SMSGateway implements NotificationGateway{
    @Override
    public void send(Notification notification) throws Exception {
        String email = notification.getRecipient().getEmail().orElseThrow(() -> new IllegalArgumentException("Email address is required for EMAIL notification."));
        System.out.println("--- Sending EMAIL ---");
        System.out.println("To: " + email);
        System.out.println("Subject: " + notification.getSubject());
        System.out.println("Body: " + notification.getMessage());
        System.out.println("---------------------\n");
    }
}

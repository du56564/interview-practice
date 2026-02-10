package interview.lld.notificationsystem;
/*
Q. A Notification System is a critical component in modern applications used to inform users about events such as new messages,
    payment updates, reminders, and alerts.

    Requirements
        EMAIL, SMS, PUSH
        Retries
        Asynchronous
        sending one notification
        Delivery should be non-blocking
        thread pool to manage parallel sending.

    Core Entities
        NotificationType
        Recipient
        Notification
        NotificationService

    Design Pattern
        Strategy
        Simple Factory
        Builder
        Decorator

 */


import interview.lld.notificationsystem.entities.Notification;
import interview.lld.notificationsystem.entities.Recipient;
import interview.lld.notificationsystem.enums.NotificationType;
import interview.lld.notificationsystem.strategy.EmailGateway;

public class MainNotificationSystem  {
    static void main() throws InterruptedException {


        NotificationService notificationService = new NotificationService(10);

        Recipient recipient1 = new Recipient("user123", "john.doe@example.com", null, "pushToken123");
        Recipient recipient2 = new Recipient("user456", null, "+15551234567", null);

        Notification welcomeEmail = new Notification.Builder(recipient1, NotificationType.EMAIL)
                .subject("Welcome!")
                .message("Welcome to notification system")
                .build();
        notificationService.sendNotification(welcomeEmail);


        Notification pushNotification = new Notification.Builder(recipient1, NotificationType.PUSH)
                .subject("New Message")
                .message("You have a new message from Jane.")
                .build();
        notificationService.sendNotification(pushNotification);


        Notification orderSms = new Notification.Builder(recipient2, NotificationType.SMS)
                .message("Your order for Digital Clock is confirmed")
                .build();
        notificationService.sendNotification(orderSms);


        Thread.sleep(1000);
        System.out.println("\nShutting down the notification system...");
        notificationService.shutdown();
        System.out.println("System shut down successfully.");

    }

}

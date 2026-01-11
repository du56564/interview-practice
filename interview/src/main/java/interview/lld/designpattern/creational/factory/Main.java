package interview.lld.designpattern.creational.factory;

public class Main {

    public static void main(String[] args) {
        NotificationCreator creator;
        creator = new SMSlNotificationCreator();
        creator.send("SMS sent");

        creator = new PushlNotificationCreator();
        creator.send("Push Notification sent.");
    }
}

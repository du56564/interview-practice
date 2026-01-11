package interview.lld.designpattern.creational.factory;

class PushlNotificationCreator extends NotificationCreator {
    @Override
    public Notification createNotification() {
        return new PushNotification();
    }
}

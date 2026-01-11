package interview.lld.designpattern.creational.factory;

class SMSlNotificationCreator extends NotificationCreator {
    @Override
    public Notification createNotification() {
        return new SMSNotification();
    }
}

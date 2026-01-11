package interview.lld.designpattern.creational.factory;
/*
Think of this class as a template:

It doesn't know what notification it's sending but it knows how to send it.
It defers the choice of notification type to its subclasses.
* */
abstract class NotificationCreator {
    public abstract Notification createNotification();
    public void send (String message) {
        Notification notification = createNotification();
        notification.send(message);
    }
}

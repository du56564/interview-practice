package interview.lld.designpattern.creational.factory;

class PushNotification implements Notification{
    @Override
    public void send (String message) {
        System.out.println("SMS Notification: "+ message);
    }
}

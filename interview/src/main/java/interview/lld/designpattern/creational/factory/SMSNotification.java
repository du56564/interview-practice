package interview.lld.designpattern.creational.factory;
//Defined Concrete Product
class SMSNotification implements Notification{
    @Override
    public void send (String message) {
        System.out.println("SMS Notification: "+ message);
    }
}

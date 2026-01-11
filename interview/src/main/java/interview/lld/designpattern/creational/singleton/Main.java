package interview.lld.designpattern.creational.singleton;

public class Main {

    public static void main (String[] args) {
        One_LazySingleton instance = One_LazySingleton.getInstance();
        instance.createConnection();

        Six_EnumSingleton.INSTANCE.createConnections();
    }
}

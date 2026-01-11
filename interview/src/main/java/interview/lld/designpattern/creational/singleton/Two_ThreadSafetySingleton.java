package interview.lld.designpattern.creational.singleton;

public class Two_ThreadSafetySingleton {
    private static volatile Two_ThreadSafetySingleton instance = null;

    private Two_ThreadSafetySingleton() {}

    public static synchronized Two_ThreadSafetySingleton getInstance() {
        if (instance == null) {
            instance = new Two_ThreadSafetySingleton();
        }
        return instance;
    }

    public void createConnection() {
        System.out.println("DB connection created.");
    }
}

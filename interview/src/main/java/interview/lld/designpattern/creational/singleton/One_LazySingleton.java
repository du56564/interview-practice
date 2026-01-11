package interview.lld.designpattern.creational.singleton;

public class One_LazySingleton {
    private static One_LazySingleton instance = null;

    private One_LazySingleton() {}

    public static One_LazySingleton getInstance() {
        if (instance == null) {
            instance = new One_LazySingleton();
        }
        return instance;
    }

    public void createConnection() {
        System.out.println("DB connection created.");
    }
}

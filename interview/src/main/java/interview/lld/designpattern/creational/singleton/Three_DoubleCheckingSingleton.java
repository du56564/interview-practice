package interview.lld.designpattern.creational.singleton;

public class Three_DoubleCheckingSingleton {
    private static Three_DoubleCheckingSingleton instance = null;

    private Three_DoubleCheckingSingleton() {}

    public static Three_DoubleCheckingSingleton getInstance() {
        if (instance == null) {
            synchronized (Three_DoubleCheckingSingleton.class) {
                instance = new Three_DoubleCheckingSingleton();
            }
        }
        return instance;
    }

    public void createConnection() {
        System.out.println("DB connection created.");
    }
}

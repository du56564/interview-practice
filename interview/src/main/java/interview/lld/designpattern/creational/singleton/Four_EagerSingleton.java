package interview.lld.designpattern.creational.singleton;
//This implementation is one of the simplest and inherently thread-safe without needing explicit synchronization.
public class Four_EagerSingleton {
    private static final Four_EagerSingleton instance = new Four_EagerSingleton();

    private Four_EagerSingleton() {}

    public static Four_EagerSingleton getInstance() {
        return instance;
    }

}

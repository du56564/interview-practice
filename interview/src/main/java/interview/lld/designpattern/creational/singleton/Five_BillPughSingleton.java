package interview.lld.designpattern.creational.singleton;

public class Five_BillPughSingleton {
    private Five_BillPughSingleton(){}

    private static class Helper {
        private static final Five_BillPughSingleton INSTANCE = new Five_BillPughSingleton();
    }

    public static Five_BillPughSingleton getInstance() {
        return Helper.INSTANCE;
    }

}

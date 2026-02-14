package interview.lld.interviewmain.designpattern;

public class DoubleCheckingSingletonPattern {

    private static DoubleCheckingSingletonPattern instance = null;

    private DoubleCheckingSingletonPattern() {}

    public static DoubleCheckingSingletonPattern getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckingSingletonPattern.class) {
                instance = new DoubleCheckingSingletonPattern();
            }
        }
        return instance;
    }

    public void createConnection() {
        System.out.println("DB connection created.");
    }
}


package interview.lld.designpattern.creational.abstractfactory;

public class Main {
    static void main(String args[]) {
        // Simulate platform detection
        String os = System.getProperty("os.name");
        GUIFactory factory;

        if (os.contains("Windows")) {
            factory = new WindowsFactory();
        } else {
            factory = new MacOsFactory();
        }

        Application app = new Application(factory);
        app.renderUI();
    }
}

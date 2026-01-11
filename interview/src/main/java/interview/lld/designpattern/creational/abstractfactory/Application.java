package interview.lld.designpattern.creational.abstractfactory;
//The client code uses the factory to create UI components. It doesn't care which OS it is dealing with.
class Application {
    private final Button button;
    private final Checkbox checkbox;

    public Application (GUIFactory factory) {
        this.button = factory.createButton();
        this.checkbox = factory.createCheckbox();
    }

    public void renderUI() {
        button.paint();
        checkbox.paint();
    }

}

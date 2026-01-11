package interview.lld.designpattern.creational.abstractfactory;

public class WindowsButton implements Button{
    @Override
    public void paint() {
        System.out.println("Painting a Windows-style button.");
    }

    @Override
    public void onClick() {
        System.out.println("Windows button clicked.");
    }
}

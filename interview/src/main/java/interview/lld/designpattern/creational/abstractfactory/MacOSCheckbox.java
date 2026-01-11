package interview.lld.designpattern.creational.abstractfactory;

class MacOSCheckbox implements Checkbox {
    @Override
    public void paint() {
        System.out.println("Painting a macOS-style checkbox.");
    }

    @Override
    public void onSelect() {
        System.out.println("MacOS checkbox selected.");
    }
}
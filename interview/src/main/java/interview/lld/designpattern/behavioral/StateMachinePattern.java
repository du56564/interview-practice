package interview.lld.designpattern.behavioral;


// A state machine handles state transitions cleanly.
// Each state knows which state comes next and what actions are valid.
// No giant switch statements checking current state in every method.
// Instead of scattered conditionals checking current state everywhere,
// you encapsulate each state's behavior in its own class.
interface VendingMachineState {
    void insertCoin (VendingMachine machine);
    void selectProduct (VendingMachine machine);
    void dispense (VendingMachine machine);
}

class NoCoinState implements VendingMachineState {
    public void insertCoin(VendingMachine machine) {
        System.out.println("Coin inserted");
        machine.setState(new HasCoinState());
    }

    public void selectProduct(VendingMachine machine) {
        System.out.println("Insert coin first");
    }

    public void dispense(VendingMachine machine) {
        System.out.println("Insert coin first");
    }
}

class HasCoinState implements VendingMachineState {
    public void insertCoin(VendingMachine machine) {
        System.out.println("Coin already inserted");
    }

    public void selectProduct(VendingMachine machine) {
        System.out.println("Product selected");
        machine.setState(new DispenseState());
    }

    public void dispense(VendingMachine machine) {
        System.out.println("Select product first");
    }
}

class DispenseState implements VendingMachineState {
    public void insertCoin(VendingMachine machine) {
        System.out.println("Please wait, dispensing");
    }

    public void selectProduct(VendingMachine machine) {
        System.out.println("Please wait, dispensing");
    }

    public void dispense(VendingMachine machine) {
        System.out.println("Dispensing product");
        machine.setState(new NoCoinState());
    }
}

class VendingMachine {
    private VendingMachineState currentState;

    public VendingMachine() {
        currentState = new NoCoinState();
    }

    public void insertCoin() {
        currentState.insertCoin(this);
    }

    public void selectProduct() {
        currentState.selectProduct(this);
    }

    public void dispense() {
        currentState.dispense(this);
    }

    public void setState(VendingMachineState state) {
        this.currentState = state;
    }
}


public class StateMachinePattern {

    static void main() {
        // Usage
        VendingMachine machine = new VendingMachine();

        machine.selectProduct();  // "Insert coin first"
        machine.insertCoin();     // "Coin inserted"
        machine.insertCoin();
        machine.selectProduct();  // "Product selected"
        machine.selectProduct();
        machine.selectProduct();
        machine.dispense();       // "Dispensing product"
        machine.dispense();
    }
}

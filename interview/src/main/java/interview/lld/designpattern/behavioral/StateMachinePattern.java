package interview.lld.designpattern.behavioral;

/*
lets an object change its behavior when its internal state changes, as if it were switching to a different class at runtime.
It’s particularly useful in situations where:
An object can be in one of many distinct states, each with different behavior.
The object’s behavior depends on current context, and that context changes over time.
You want to avoid large, monolithic if-else or switch statements that check for every possible state.

vending machine system: accept money, dispense products, and go back to idle.

*** Avoid if-else and switches for states
State
    - Idle
    - ItemSelected
    - HashMoney
    - Dispensing
    - Operation for all : selectItem, insertCoin, dispenseItem

Entity
    - State : MachineState
    - ConcreteState : IdleState, ItemSelectedState
    - Context : VendingMachine
    - Client : Usage

 */



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

/*
Output:
Insert coin first
Coin inserted
Coin already inserted
Product selected
Please wait, dispensing
Please wait, dispensing
Dispensing product
Insert coin first

 */

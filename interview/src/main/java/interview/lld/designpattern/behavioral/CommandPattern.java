package interview.lld.designpattern.behavioral;

/*
turns a request into a standalone object, allowing you to parameterize actions, queue them, log them,
or support undoable operations all while decoupling the sender from the receiver.
Example:
Smart home controller
This way, the controller, remote, or scheduler doesn’t care how a command works — it just knows which command to execute.

Instead of calling methods directly on device classes, you encapsulate each request
(like light.on() or thermostat.setTemperature(22)) as a Command object.

Parameterize actions
Queue or log operations
Support undo/redo
Decouple the invoker of an operation from the receiver that performs it

Core Entity
    - Command : execute()
    - ConcreteCommand : Implement execute(), reference Receiver
    - Receiver : trigger command, business logic
    - Invoker : init execute()
    - Client

 */

import java.util.Stack;

interface Command {
    void execute();
    void undo();
}

//Receiver
class Light {
    public void on() {
        System.out.println("Light On");
    }
    public void off() {
        System.out.println("Light Off");
    }
}
//Receiver
class Thermostat {
    private int currentTemperature = 20; // default
    public void setTemperature(int temp) {
        System.out.println("Thermostat set to " + temp + "°C");
        currentTemperature = temp;
    }
    public int getCurrentTemperature() {
        return currentTemperature;
    }
}

//Concrete Command
class LightOnCommand implements Command {
    private final Light light;

    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.on();
    }

    @Override
    public void undo() {
        light.off();
    }
}
class LightOffCommand implements Command {
    private final Light light;

    public LightOffCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.off();
    }

    @Override
    public void undo() {
        light.on();
    }
}

class SetTemperatureCommand implements Command {
    private final Thermostat thermostat;
    private final int newTemperature;
    private int previousTemperature;

    public SetTemperatureCommand(Thermostat thermostat, int temperature) {
        this.thermostat = thermostat;
        this.newTemperature = temperature;
    }

    @Override
    public void execute() {
        previousTemperature = thermostat.getCurrentTemperature();
        thermostat.setTemperature(newTemperature);
    }

    @Override
    public void undo() {
        thermostat.setTemperature(previousTemperature);
    }
}

// Invoker
class SmartButton {
    private Command currentCommand;
    private final Stack<Command> history = new Stack<>();

    public void setCommand (Command command) {
        this.currentCommand = command;
    }

    public void press() {
        if (currentCommand != null) {
            currentCommand.execute();
            history.push(currentCommand);
        } else {
            System.out.println("No command assigned.");
        }
    }

    public void undoLast() {
        if (!history.isEmpty()) {
            Command lastCommand = history.pop();
            lastCommand.undo();
        } else {
            System.out.println("Nothing to undo.");
        }
    }
}



public class CommandPattern {
    static void main() {

        //Receiver
        Light light = new Light();
        Thermostat thermostat = new Thermostat();

        // Command
        Command lightOn = new LightOnCommand(light);
        Command lightOff = new LightOffCommand(light);
        Command setTemp22 = new SetTemperatureCommand(thermostat, 22);

        // Invoker
        SmartButton button = new SmartButton();

        //Simulate usage
        // Simulate usage
        System.out.println("→ Pressing Light ON");
        button.setCommand(lightOn);
        button.press();

        System.out.println("→ Pressing Set Temp to 22°C");
        button.setCommand(setTemp22);
        button.press();

        System.out.println("→ Pressing Light OFF");
        button.setCommand(lightOff);
        button.press();

        // Undo sequence
        System.out.println("\n↶ Undo Last Action");
        button.undoLast();  // undo Light OFF

        System.out.println("↶ Undo Previous Action");
        button.undoLast();  // undo Set Temp

        System.out.println("↶ Undo Again");
        button.undoLast();  // undo Light ON


    }
}

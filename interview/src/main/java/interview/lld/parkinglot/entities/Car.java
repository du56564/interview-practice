package interview.lld.parkinglot.entities;

import interview.lld.parkinglot.enums.VehicleSize;

public class Car extends Vehicle {
    public Car(String licenseNumber) {
        super(licenseNumber, VehicleSize.MEDIUM);
    }
}

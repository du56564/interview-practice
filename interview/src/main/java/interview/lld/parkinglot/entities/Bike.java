package interview.lld.parkinglot.entities;

import interview.lld.parkinglot.enums.VehicleSize;

public class Bike extends Vehicle {
    public Bike(String licenseNumber) {
        super(licenseNumber, VehicleSize.SMALL);
    }
}

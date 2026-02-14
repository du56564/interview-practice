package interview.lld.parkinglot.entities;

import interview.lld.parkinglot.enums.VehicleSize;

public class Truck extends Vehicle {
    public Truck(String licenseNumber) {
        super(licenseNumber, VehicleSize.LARGE);
    }
}

package interview.lld.parkinglot;

import interview.lld.parkinglot.entities.*;
import interview.lld.parkinglot.enums.VehicleSize;
import interview.lld.parkinglot.strategies.VehicleBasedFeeStrategy;

import java.util.Optional;

/*
A parking lot is a designated area where vehicles can be parked temporarily, either in public or private spaces.
Categories : Compact, Regular, Large
Issue Parking ticket to allocated car parked.

Requirements:
    Functional
    - Multi-level parking
    - Vehicle Type : Bikes, Cards, Trucks
    - Classify parking spots by size
    - Automatic allocation
    - Display Open slots
    - Issue parking ticket based on Track entry & exit time

    Non-Functional
    - concurrent entry/exit
    - code should be thread safe

Core Entity
    - Vehicle : Base class with license plate and size
    - VehicleSize : Vehicle/spot sizes: SMALL, MEDIUM, LARGE
    - ParkingSpot : Manages individual spot state
    - ParkingFloor : Groups spots by floor
    - ParkingLot : Orchestrates entire system
    - ParkingTicket : Tracks parking session
    - FeeStrategy : Contract for fee calculation
        - FlatRateFeeStrategy
        - HourlyFeeStrategy
        - VehicleBasedFeeStrategy
    - SpotAllocationStrategy : Contract for spot selection
    - CustomException

Design Pattern
    - Singleton
    - Strategy
    - Thread Safety
    - Facade

ParkingLot
    - Floor1
        - SpotA1, SpotA2, SportA3
    - Floor2
        - SportB1, SportB2, SportB3
    .
    .
    FloorN
        - SpotN1..




 */
public class MainParkingLot {

    static void main() {
        ParkingLot parkingLot = ParkingLot.getInstance();

        // 1. Initialize the parking lot with floors and spots
        ParkingFloor floor1 = new ParkingFloor(1);
        floor1.addSpot(new ParkingSpot("F1-S1", VehicleSize.SMALL));
        floor1.addSpot(new ParkingSpot("F1-M1", VehicleSize.MEDIUM));
        floor1.addSpot(new ParkingSpot("F1-L1", VehicleSize.LARGE));

        ParkingFloor floor2 = new ParkingFloor(2);
        floor2.addSpot(new ParkingSpot("F2-M1", VehicleSize.MEDIUM));
        floor2.addSpot(new ParkingSpot("F2-M2", VehicleSize.MEDIUM));

        parkingLot.addFloor(floor1);
        parkingLot.addFloor(floor2);

        parkingLot.setFeeStrategy(new VehicleBasedFeeStrategy());

        // 2. Simulate vehicle entries
        System.out.println("\n--- Vehicle Entries ---");
        floor1.displayAvailability();
        floor2.displayAvailability();

        Vehicle bike = new Bike("B-123");
        Vehicle car = new Car("C-456");
        Vehicle truck = new Truck("T-789");

        Optional<ParkingTicket> bikeTicketOpt = parkingLot.parkVehicle(bike);

        Optional<ParkingTicket> carTicketOpt = parkingLot.parkVehicle(car);

        Optional<ParkingTicket> truckTicketOpt = parkingLot.parkVehicle(truck);

        System.out.println("\n--- Availability after parking ---");
        floor1.displayAvailability();
        floor2.displayAvailability();

        // 3. Simulate another car entry (should go to floor 2)
        Vehicle car2 = new Car("C-999");
        Optional<ParkingTicket> car2TicketOpt = parkingLot.parkVehicle(car2);

        // 4. Simulate a vehicle entry that fails (no available spots)
        Vehicle bike2 = new Bike("B-000");
        Optional<ParkingTicket> failedBikeTicketOpt = parkingLot.parkVehicle(bike2);

        // 5. Simulate vehicle exits and fee calculation
        System.out.println("\n--- Vehicle Exits ---");

        if (carTicketOpt.isPresent()) {
            Optional<Double> feeOpt = parkingLot.unparkVehicle(car.getLicenseNumber());
            feeOpt.ifPresent(fee -> System.out.printf("Car C-456 unparked. Fee: $%.2f\n", fee));
        }

        System.out.println("\n--- Availability after one car leaves ---");
        floor1.displayAvailability();

    }

}


/*

--- Vehicle Entries ---
--- Floor 1 Availability ---
  SMALL spots: 1
  MEDIUM spots: 1
  LARGE spots: 1
--- Floor 2 Availability ---
  SMALL spots: 0
  MEDIUM spots: 2
  LARGE spots: 0
B-123 parked at F1-S1. Ticket: 4dd336ce-65e8-4f29-a02b-6e006fd6fc07
C-456 parked at F1-M1. Ticket: 40976b36-87fe-4bf5-af69-5bef85ce4303
T-789 parked at F1-L1. Ticket: 72821fc3-360b-48af-8c78-e4859bc64441

--- Availability after parking ---
--- Floor 1 Availability ---
  SMALL spots: 0
  MEDIUM spots: 0
  LARGE spots: 0
--- Floor 2 Availability ---
  SMALL spots: 0
  MEDIUM spots: 2
  LARGE spots: 0
C-999 parked at F2-M1. Ticket: 82fabe40-1e4f-4d5d-8061-553fb5394236
No available spot for B-000

--- Vehicle Exits ---
Car C-456 unparked. Fee: $20.00

--- Availability after one car leaves ---
--- Floor 1 Availability ---
  SMALL spots: 0
  MEDIUM spots: 1
  LARGE spots: 0


 */
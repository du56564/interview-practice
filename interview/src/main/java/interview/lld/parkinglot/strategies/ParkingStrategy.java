package interview.lld.parkinglot.strategies;

import interview.lld.parkinglot.entities.ParkingFloor;
import interview.lld.parkinglot.entities.ParkingSpot;
import interview.lld.parkinglot.entities.Vehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface ParkingStrategy {
    Optional<ParkingSpot> findSpot(List<ParkingFloor> floors, Vehicle vehicle);
}




package interview.lld.parkinglot.strategies;

import interview.lld.parkinglot.entities.ParkingTicket;
import interview.lld.parkinglot.enums.VehicleSize;

import java.util.Map;

public interface FeeStrategy {
    double calculateFee(ParkingTicket parkingTicket);
}


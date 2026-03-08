package interview.lld.interviewmain.lld;

/*
A parking lot is a designated area where vehicles can be parked temporarily, either in public or private spaces.

Requirement:
	•	Multiple floors
	•	Different spot types (Bike, Car, Truck)
	•	Vehicle entry → Ticket generation
	•	Vehicle exit → Fee calculation
	•	Spot release
	•	Thread-safe allocation

support:
	•	Multiple floors
	•	Different spot types (Bike, Car, Truck)
	•	Vehicle entry → Ticket generation
	•	Vehicle exit → Fee calculation
	•	Spot release
	•	Thread-safe allocation


Demonstrated:
	•	✔ Entity modeling
	•	✔ Enums
	•	✔ Thread safety
	•	✔ Strategy pattern
	•	✔ Clean service layer
	•	✔ Ticket lifecycle
	•	✔ Separation of concerns

 */


import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

enum VehicleType {
    BIKE, CAR, TRUCK
}
enum SpotType {
    BIKE, CAR, TRUCK
}

enum TicketStatus {
    PAID, ACTIVE
}

class ParkingVehicle {
    private final String number;
    private final VehicleType type;

    public ParkingVehicle(String number, VehicleType type) {
        this.number = number;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public VehicleType getType() {
        return type;
    }
}

class ParkingSpot {
    private final String id;
    private final SpotType type;
    private boolean occupied;
    private ParkingVehicle vehicle;

    public ParkingSpot(String id, SpotType type) {
        this.id = id;
        this.type = type;
    }

    public synchronized boolean park (ParkingVehicle vehicle) {
        if (occupied) return false;
        this.vehicle = vehicle;
        this.occupied = true;
        return true;
    }

    public synchronized void leave () {
        this.vehicle = null;
        this.occupied = false;
    }

    public String getId() {
        return id;
    }

    public SpotType getType() {
        return type;
    }

    public boolean isAvailable() {
        return !occupied;
    }
}

class ParkingFloor {
    private final String id;
    private final List<ParkingSpot> spots;

    public ParkingFloor(String id, List<ParkingSpot> spots) {
        this.id = id;
        this.spots = spots;
    }

    public Optional<ParkingSpot> findAvailableSpot (SpotType type) {
        return spots.stream()
                .filter(s -> s.getType().equals(type) && s.isAvailable())
                .findFirst();
    }

    public String getId() {
        return id;
    }
}

class Ticket {
    private final String id;
    private final ParkingVehicle vehicle;
    private final ParkingSpot spot;
    private final long entryTime;
    private long exitTime;
    private TicketStatus status;

    public Ticket(String id, ParkingVehicle vehicle, ParkingSpot spot) {
        this.id = id;
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = System.currentTimeMillis();
        this.status = TicketStatus.ACTIVE;
    }
    public void close() {
        this.exitTime = System.currentTimeMillis();
        this.status = TicketStatus.PAID;
    }

    public long getDurationMinutes() {
        return (exitTime - entryTime) / (1000*60);
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public String getId() {
        return id;
    }
}

interface ParkingPricingStrategy {
    double calculateFees (long minutes, VehicleType type);
}

class HourlyParkingPricingStrategy implements ParkingPricingStrategy{
    @Override
    public double calculateFees(long minutes, VehicleType type) {
        double hourlyRate = switch (type) {
            case BIKE -> 10;
            case CAR -> 20;
            case TRUCK -> 30;
        };
        double hours = Math.ceil(minutes/ 60.0);
        return hours * hourlyRate;
    }
}

class ParkingLotService {
    private final Map<String, Ticket> activeTickets = new ConcurrentHashMap<>();
    private final List<ParkingFloor> floors;
    private final ParkingPricingStrategy pricingStrategy;

    public ParkingLotService(List<ParkingFloor> floors, ParkingPricingStrategy pricingStrategy) {
        this.floors = floors;
        this.pricingStrategy = pricingStrategy;
    }

    public Optional<Ticket> parkVehicle(ParkingVehicle vehicle) {
        SpotType sportType = SpotType.valueOf(vehicle.getType().name());
        for (ParkingFloor floor: floors) {
            Optional<ParkingSpot> spotOpt = floor.findAvailableSpot(sportType);
            if (spotOpt.isPresent()) {
                ParkingSpot spot = spotOpt.get();
                if (!spot.isAvailable()) {
                    continue;
                }
                Ticket ticket = new Ticket(UUID.randomUUID().toString(), vehicle, spot);
                activeTickets.put(ticket.getId(), ticket);
                return Optional.of(ticket);
            }
        }
        return Optional.empty();
    }

    public double exitVehicle(String ticketId) {
        Ticket ticket = activeTickets.get(ticketId);
        if (ticket == null) {
            throw new  IllegalArgumentException("Invalid ticket");
        }
        ticket.close();

        double fee = pricingStrategy.calculateFees(
                ticket.getDurationMinutes(),
                        ticket.getSpot().getType().equals(SpotType.BIKE) ? VehicleType.BIKE :
                        ticket.getSpot().getType().equals(SpotType.CAR) ? VehicleType.CAR : VehicleType.TRUCK
        );
        ticket.getSpot().leave();
        activeTickets.remove(ticketId);
        return fee;
    }

}


public class LLDParkingLot {
    static void main() {

        List<ParkingSpot> floor1Spots = List.of(
                new ParkingSpot("F1-S1", SpotType.CAR),
                new ParkingSpot("F1-S2", SpotType.BIKE),
                new ParkingSpot("F1-S3", SpotType.TRUCK)
        );
        ParkingFloor floor1 = new ParkingFloor("F1", floor1Spots);

        ParkingLotService parkingLot = new ParkingLotService(List.of(floor1), new HourlyParkingPricingStrategy());

        ParkingVehicle parkingVehicle = new ParkingVehicle("KA01AB1234", VehicleType.CAR);
        Optional<Ticket> ticketOpt = parkingLot.parkVehicle(parkingVehicle);

        ticketOpt.ifPresentOrElse(
                ticket -> {
                    System.out.printf("Vehicle Parked. Ticket Id: %s \n", ticket.getId());
                    try { Thread.sleep(2000); } catch (Exception ignored) {}
                    double fee = parkingLot.exitVehicle(ticket.getId());
                }, () -> {
                    System.out.println("No spot available");
                });
    }
}


/*
Vehicle Parked. Ticket Id: afa6de8e-2022-4cb7-942b-eabf096eff98
 */
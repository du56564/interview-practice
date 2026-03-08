package interview.lld.interviewmain.lld;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/*
A ride-hailing service is a digital platform that enables users to request on-demand transportation by connecting them
 with nearby drivers through a mobile or web application.
Book rides in real time
Match with available drivers based on location
Track ride progress via GPS
Make cashless payments
Rate and review trips after completion


Rider -> Search -> Ride -> Booking -> Ride
Driver -> Offer -> Ride
Driver Start Ride
Rider track the ride
payment done
provide rating

riders request ride (pickup,dropoff, ride type)
notify to driver : Accept/ Reject
Driver start and end ride
Update Trip status
Trip history for both



Entity
    Model
    - TripStatus
    - DriverStatus
    - RideType
    - Location
    - Vehicle
    - User
        - Rider
        - Driver
    - Trip
    Matching Strategy
    - DriverMatchingStrategy: +findDriver
        - NearestDriverMatchingStrategy
    - PricingStrategy
        - FlatRatePricingStrategy
    Core Service
    - RideSharingService
    Main
    - MainRideHailingService

Covers:-
✔ Strategy Pattern
✔ State modeling
✔ Concurrency
✔ Extensible pricing
✔ Extensible matching
✔ Clean object model


 */
enum TripStatus {
    REQUESTED,
    ASSIGNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

enum DriverStatus {
    ONLINE,
    IN_TRIP,
    OFFLINE
}

enum RideType {
    SEDAN,
    SUV,
    AUTO
}

class Location {
    private final double latitude;
    private final double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double distanceTo (Location other) {
        return Math.sqrt(Math.pow(latitude - other.latitude, 2)
                + Math.pow(longitude - other.longitude, 2));
    }
}

class Vehicle {
    private final String mode;
    private final String licenseNumber;
    private final RideType type;

    public Vehicle(String mode, String licenseNumber, RideType type) {
        this.mode = mode;
        this.licenseNumber = licenseNumber;
        this.type = type;
    }

    public RideType getType() {
        return type;
    }
}

abstract class UserE {
    protected final String id;
    protected final String name;

    public UserE(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

class Rider extends UserE {

    public Rider(String id, String name) {
        super(id, name);
    }
}

class Driver extends UserE {
    private final Vehicle vehicle;
    private volatile Location location;
    private volatile DriverStatus status;

    public Driver(String id, String name, Vehicle vehicle, Location location) {
        super(id, name);
        this.vehicle = vehicle;
        this.location = location;
        this.status = DriverStatus.ONLINE;
    }

    public synchronized boolean assignTrip() {
        if (status == DriverStatus.ONLINE) {
            status = DriverStatus.IN_TRIP;
            return true;
        }
        return false;
    }

    public synchronized void completeTrip() {
        status = DriverStatus.ONLINE;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public Location getLocation() {
        return location;
    }

    public DriverStatus getStatus() {
        return status;
    }

}

class Trip {
    private final String id;
    private final Rider rider;
    private Driver driver;
    private final Location pickup;
    private final Location drop;
    private volatile TripStatus status;
    private double fare;

    public Trip(String id, Rider rider, Location pickup, Location drop) {
        this.id = id;
        this.rider = rider;
        this.pickup = pickup;
        this.drop = drop;
        status = TripStatus.REQUESTED;
    }

    public synchronized boolean assignDriver(Driver driver) {
        if (status == TripStatus.REQUESTED) {
            status = TripStatus.ASSIGNED;
            this.driver = driver;
            return true;
        }
        return false;
    }

    public void start() {
        if (status == TripStatus.ASSIGNED) {
            status = TripStatus.IN_PROGRESS;
        }
    }

    public void complete() {
        if (status == TripStatus.IN_PROGRESS) {
            status = TripStatus.COMPLETED;
        }
    }

    public void setFare (double fare) {
        this.fare = fare;
    }

    public TripStatus getStatus() {
        return status;
    }

    public Location getPickup() {
        return pickup;
    }

    public String getId() {
        return id;
    }
}

interface DriverMatchingStrategy {
    Optional<Driver> findDriver(List<Driver> drivers, Location pickup, RideType rideType);
}

class NearestDriverMatchingStrategy implements DriverMatchingStrategy {
    @Override
    public Optional<Driver> findDriver(List<Driver> drivers, Location pickup, RideType rideType) {
        return drivers.stream()
                .filter(d -> d.getStatus().equals(DriverStatus.ONLINE))
                .filter(d -> d.getVehicle().getType().equals(rideType))
                .min(Comparator.comparingDouble(d -> d.getLocation().distanceTo(pickup)));
    }
}

interface PricingStrategy {
    double calculateFare(Location pickup, Location drop, RideType rideType);
}

class FlatRatePricingStrategy implements PricingStrategy {
    @Override
    public double calculateFare(Location pickup, Location drop, RideType rideType) {
        double base = 50;
        double perKM = switch (rideType) {
            case SUV -> 15;
            case SEDAN -> 10;
            case AUTO -> 7;
        };
        double distance = pickup.distanceTo(drop);
        return base + (distance * perKM);
    }
}


class RideSharingService {
    private final Map<String, Rider> riders = new ConcurrentHashMap<>();
    private final Map<String, Driver> drivers = new ConcurrentHashMap<>();
    private final Map<String, Trip> trips = new ConcurrentHashMap<>();

    private final DriverMatchingStrategy matchingStrategy;
    private final PricingStrategy pricingStrategy;

    public RideSharingService(DriverMatchingStrategy matchingStrategy, PricingStrategy pricingStrategy) {
        this.matchingStrategy = matchingStrategy;
        this.pricingStrategy = pricingStrategy;
    }

    public Rider registerRider(String id, String name) {
        Rider rider = new Rider(id, name);
        riders.put(id, rider);
        return rider;
    }

    public Driver registerDriver (String id, String name, Vehicle vehicle, Location location) {
        Driver driver = new Driver(id, name, vehicle, location);
        drivers.put(id, driver);
        return driver;
    }

    public Optional<Trip> requestTrip(String riderId, Location pickup, Location drop, RideType rideType) {

        Rider rider = riders.get(riderId);
        if (rider == null) return Optional.empty();

        Trip trip = new Trip(UUID.randomUUID().toString(), rider, pickup, drop);

        Optional<Driver> driverOpt = matchingStrategy.findDriver(new ArrayList<>(drivers.values()), pickup, rideType);

        if (driverOpt.isEmpty()) return Optional.empty();

        Driver driver = driverOpt.get();

        synchronized (driver) {
            if (!driver.assignTrip()) return Optional.empty();
            if (!trip.assignDriver(driver))  {
                driver.completeTrip();
                return Optional.empty();
            }
        }

        double fare = pricingStrategy.calculateFare(pickup, drop, rideType);
        trip.setFare(fare);
        trips.put(trip.getId(), trip);
        return Optional.of(trip);
    }
}

public class LLDRideHailingService {
    static void main() {
        // Create strategies
        DriverMatchingStrategy matchingStrategy =
                new NearestDriverMatchingStrategy();

        PricingStrategy pricingStrategy =
                new FlatRatePricingStrategy();

        // Create service
        RideSharingService service =
                new RideSharingService(matchingStrategy, pricingStrategy);

        // Register Riders
        Rider rider1 = service.registerRider("R1", "Alice");

        // Register Drivers
        Vehicle vehicle1 = new Vehicle("Honda City", "KA01AB1234", RideType.SEDAN);

        Vehicle vehicle2 = new Vehicle("Toyota Innova", "KA02CD5678", RideType.SUV);

        service.registerDriver("D1", "John", vehicle1, new Location(10, 10));

        service.registerDriver("D2", "Mike", vehicle2, new Location(12, 12));

        //Request Trip
        Optional<Trip> tripOpt = service.requestTrip(rider1.id, new Location(11, 11), new Location(22, 22), RideType.SEDAN);

        tripOpt.ifPresentOrElse( trip -> {
                    System.out.println("Trip Assigned!");
                    System.out.println("Trip ID: " + trip.getId());
                    System.out.println("Status: " + trip.getStatus());

                    // Start Trip
                    trip.start();
                    System.out.println("Trip Started. Status: " + trip.getStatus());

                    // Complete Trip
                    trip.complete();
                    System.out.println("Trip Completed. Status: " + trip.getStatus());
                } , () -> {
                    System.out.println("No driver available");
                }
        );

    }

}


/*
Output:

Trip Assigned!
Trip ID: 940bc902-e8bd-4f1b-9dd9-80bf3bad813c
Status: ASSIGNED
Trip Started. Status: IN_PROGRESS
Trip Completed. Status: COMPLETED

 */
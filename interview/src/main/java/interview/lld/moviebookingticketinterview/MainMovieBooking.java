package interview.lld.moviebookingticketinterview;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/*
Design a movie ticket booking system similar to BookMyShow that allows users to browse movies,
select theaters and show times, book tickets, and manage reservations.

Entities
    - User
    - Show
    - Seat
    - Booking
    - BookingService
    - SeatLockManager
    - PaymentStrategy (just one interface)
    - Concurrency handling
        - Acquire lock, release lock
        - State modeling
                - AVAILABLE → LOCKED → BOOKED
                - LOCKED → AVAILABLE   (on failure))

Relation
BookingService -> User, Show, Seat, Booking
Show -> User
Seat -> User, Show
Seat -> SeatStatus
Booking -> BookingStatus, user, show, <To book>seats
BookingService -> Show -> Booking -> for User -> having seatIds
 */



/*enum SeatStatus {
    AVAILABLE,
    LOCKED,
    BOOKED
}

enum BookingStatus {
    PENDING,
    CONFIRMED,
    FAILED
}

// Model: Start

class User {
    private final String id;
    private final String name;
    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }
    public String getId() { return id; }
    public String getName() { return name; }
}

class Seat {
    private final String id;
    private SeatStatus status;
    private final ReentrantLock lock = new ReentrantLock();

    public Seat(String id) {
        this.id = id;
        this.status = SeatStatus.AVAILABLE;
    }
    public String getId() {
        return id;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void confirmBooking() {
        lock.lock();
        try {
            status = SeatStatus.BOOKED;
        } finally {
            lock.unlock();
        }
    }

    public boolean tryLockSeat() {
        lock.lock();
        try {
            if (status == SeatStatus.AVAILABLE) {
                status = SeatStatus.LOCKED;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        lock.lock();
        try {
            status = SeatStatus.AVAILABLE;
        } finally {
            lock.unlock();
        }
    }
}

class Show {
    private final String id;
    private final Map<String, Seat> seats = new HashMap<>();
    public Show(String id, int totalSeats) {
        this.id = id;
        for (int i = 1; i <= totalSeats; i++) {
            seats.put("S"+i, new Seat("S"+i));
        }
    }
    public String getId() {
        return id;
    }
    public Seat getSeat(String id) {
        return seats.get(id);
    }
    public Collection<Seat> getSeats() {
        return seats.values();
    }
}

class Booking {
    private final String id;
    private final User user;
    private final Show show;
    private final List<Seat> seats;
    private BookingStatus status;

    public Booking(String id, User user, Show show, List<Seat> seats) {
        this.id = id;
        this.user = user;
        this.show = show;
        this.seats = seats;
        this.status = BookingStatus.PENDING;
    }

    public void confirm() {
        status = BookingStatus.CONFIRMED;
    }
    public void fail() {
        this.status = BookingStatus.FAILED;
    }
    public BookingStatus getStatus() { return status; }
    public String getId() { return id; }
}
// Model : End


// PAYMENT : Start
interface PaymentService {
    boolean processPayment(double amount);
}
class DummyPaymentService implements PaymentService {
    @Override
    public boolean processPayment(double amount) {
        System.out.println("Processing payment of $" + amount);
        return true;
    }
}
// PAYMENT : End


// Service : Start
class BookingService {
    private final Map<String, Show> shows = new ConcurrentHashMap<>();
    private final PaymentService paymentService = new DummyPaymentService();

    public void addShow(Show show) {
        shows.put(show.getId(), show);
    }
    public Optional<Booking> bookSeats(User user, Show show, List<String> seatIds) {
        //Show show = shows.get(showId);
        if (show == null) {
            return Optional.empty();
        }
        List<Seat> lockedSeats = new ArrayList<>();

        // Step 1: Try locking all seats
        for (String seatId : seatIds) {
            Seat seat = show.getSeat(seatId);
            if (seat == null || !seat.tryLockSeat()) {
                // Release already locked seats
                for (Seat s : lockedSeats) {
                    s.release();
                }
            }
            lockedSeats.add(seat);
        }

        // Step 2: Process payment
        double amount = seatIds.size() * 10.0;
        boolean paymentSuccess = paymentService.processPayment(amount);
        Booking booking = new Booking(UUID.randomUUID().toString(), user, show, lockedSeats);
        if (paymentSuccess) {
            // Step 3: Confirm seats
            for (Seat seat: lockedSeats) {
                seat.confirmBooking();
            }
            booking.confirm();
            return Optional.of(booking);
        } else {
            // Step 4: Release seats
            for (Seat seat : lockedSeats) {
                seat.release();
            }
            booking.fail();
            return  Optional.empty();
        }
    }

}
// Service : End



public class MainMovieBooking {
    static void main() {
        BookingService bookingService = new BookingService();
        Show show = new Show("Show1", 10);
        bookingService.addShow(show);

        User aliceUser = new User("U1", "Alice");

        Optional<Booking> booking = bookingService.bookSeats(aliceUser, show, List.of("S1", "S2", "S3"));

        if (booking.isPresent()) {
            System.out.println("Booking Successful. ID: " + booking.get().getId());
        } else {
            System.out.println("Booking Failed.");
        }

    }
}


 */

package interview.lld.practice;

/*
Movie Booking System

Functional Requirements
	1.	User can search/browse movies.
	2.	User can view cinema running a selected movie.
	3.	User can view available shows (date & time).
	4.	User can select seats.
	5.	Selected seats should be temporarily locked.
	6.	User can make payment.
	7.	On successful payment, booking is confirmed.
	8.	On payment failure, locked seats are released.

Entity
    - User
    - Movie
    - Cinema
    - Screen
    - Show
    - Seat
    - Payment
    - Booking
    Service Layer
    - BookingService
    Entry Layer
    - Main

flow
Search Movie
→ Select Cinema
→ Select Show (date & time)
→ Select Seats
    - Lock seat if AVAILABLE
→ Make Payment
→ Confirm Booking
→ Create Booking Record
 */


import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

enum SeatStatus {
    // manager state
    LOCKED,
    AVAILABLE,
    BOOKED
}
enum BookingStatus {
    CREATED,
    CONFIRMED,
    CANCELLED,
    FAILED
}

class User {
    private final String id;
    private final String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

class Movie {
    private final String id;
    private final String name;

    public Movie(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

class Cinema {
    private final String id;
    private final String name;
    private final List<Screen> screens;

    public Cinema(String id, String name) {
        this.id = id;
        this.name = name;
        this.screens = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addScreen(Screen screen) {
        screens.add(screen);
    }

    public List<Screen> getScreens() {
        return screens;
    }

}

class Screen {
    private final String id;
    private List<Seat>  seats;

    public Screen(String id, int totalSeats) {
        this.id = id;
        seats = new ArrayList<>();
        for (int i = 1; i <= totalSeats; i++) {
            seats.add(new Seat("S"+i));
        }
    }

    public String getId() {
        return id;
    }

    public List<Seat> getSeats() {
        return seats;
    }

}

class Seat {
    private final String id;
    private SeatStatus status;
    private final ReentrantLock lock;

    public Seat(String id) {
        this.id = id;
        status = SeatStatus.AVAILABLE;
        lock = new ReentrantLock();
    }

    public String getId() {
        return id;
    }

    //First try to acquire lock before booking confirm seat
    public boolean tryAcquireLock() {
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
    public void confirm() {
        lock.lock();
        try {
            if (status == SeatStatus.LOCKED) {
                status = SeatStatus.BOOKED;
            }
        } finally {
            lock.unlock();
        }
    }

    public void release() {
        lock.lock();
        try {
            if (status == SeatStatus.LOCKED) {
                status = SeatStatus.AVAILABLE;
            }
        } finally {
            lock.unlock();
        }
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void cancelBooking() {
        lock.lock();
        try {
            if (status == SeatStatus.BOOKED) {
                status = SeatStatus.AVAILABLE;
            }
        } finally {
            lock.unlock();
        }
    }

}


class Show {
    private final String id;
    private final Movie movie;
    private final Screen screen;
    private final LocalDateTime startTime;

    public Show(String id, Movie movie, Screen screen, LocalDateTime startTime) {
        this.id = id;
        this.movie = movie;
        this.screen = screen;
        this.startTime = startTime;
    }
    public String getId() { return id; }
    public Movie getMovie() { return movie; }
    public Screen getScreen() { return screen; }
    public LocalDateTime getStartTime() { return startTime; }

}

// Booking
class Booking {
    private final String id;
    private final User user;
    private final Show show;
    private final List<Seat> seats;
    private BookingStatus status;

    public Booking(User user, Show show, List<Seat> seats) {
        this.id = UUID.randomUUID().toString();
        this.user = user;
        this.show = show;
        this.seats = seats;
        this.status = BookingStatus.CREATED;
    }

    public String getId () {
        return id;
    }

    public void confirm() {
        status = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (status == BookingStatus.CONFIRMED) {
            status = BookingStatus.CANCELLED;
            seats.forEach(Seat::release); // Important: release cancelled seat
        }
    }

    public BookingStatus getStatus() {
        return status;
    }
}

interface PaymentService {
    public boolean processPayment(int amount);
}
class OnlinePaymentService implements  PaymentService {
    @Override
    public boolean processPayment(int amount) {
        System.out.println("Payment is successful of amount:"+amount);
        return true;
    }
}
class BookingService {
    private final PaymentService paymentService = new OnlinePaymentService();
    private final Map<String, Show> shows = new ConcurrentHashMap<>();

    public void addShow (Show show) {
        shows.put(show.getId(), show);
    }

    public List<Movie> browseMovie(Collection<Show> shows) {
        return shows.stream().map(Show::getMovie).distinct().collect(Collectors.toList());
    }

    public Optional<Booking> book(User user, String showId, List<String> seatIds) {
        Show show = shows.get(showId);
        if (show == null) return  Optional.empty();
        List<Seat> lockedSeats = new ArrayList<>(); // for later to release after payment success
        //Deadlock prevention: sort seats
        seatIds.sort(String::compareTo);
        for (String seatId : seatIds) { // lock seats based on seatId requested by usr to book
            Seat seat = show.getScreen().getSeats()
                    .stream()
                    .filter(s -> s.getId().equalsIgnoreCase(seatId))
                    .findFirst()
                    .orElse(null);
            if (seat == null || !seat.tryAcquireLock()) {
                lockedSeats.forEach(Seat::release);
                return Optional.empty();
            }
            lockedSeats.add(seat);
        }

        boolean paymentSuccess = paymentService.processPayment(seatIds.size() * 100);

        Booking booking = new Booking(user, show, lockedSeats);

        if(paymentSuccess) {
            lockedSeats.forEach(Seat::confirm); // update seat as confirmed
            booking.confirm();
            return Optional.of(booking);
        } else {
            lockedSeats.forEach(Seat::release); // else release acquired lock
            return Optional.empty();
        }

    }

}

class Practice {
    public static void main(String[] args) {
        Movie movie = new Movie("M1", "3 IDIOTS");
        Cinema cinema = new Cinema("T1", "PVR"); // Cinema -> Screens
        Screen screen = new Screen("SC1", 10); // Screen -> seats

        cinema.addScreen(screen);

        Show show = new Show("SHOW1", movie, screen, LocalDateTime.now()); //Show -> Movie, Screen, LastTimeout

        BookingService bookingService = new BookingService(); // BookingService -> Booking(user, showId, seatIds), PaymentService
        bookingService.addShow(show);

        User user = new User("U1", "Deepak");

        bookingService.book(user, "SHOW1", Arrays.asList("S1", "S2"))
                .ifPresentOrElse(b -> System.out.println("Booking Confirm: "+ b.getId())
                        , () -> System.out.println("Booking Failed!"));

    }
}

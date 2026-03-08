package interview.lld.interviewmain.lld;

/*
Design a movie ticket booking system similar to BookMyShow that allows users to browse movies,
select theaters and show times, book tickets, and manage reservations.

BrowseMovie
select Cinema
Show times
book ticket
manage reservation

User
Movie
Theater -> Screen* -> Seats* -> Booking Seat if payment success -> Block Seat -> Book Seat -> Release Lock -> Respond booking details




Entity
    Models:
        Movie
        User
        theater
        Screen
        Seat
        Booking
        Show
        BookingStatus
        SeatStatus
     Service:
        BookingService
     EntryPoint:
        Practice2

 */


import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

enum SeatStatus {
    LOCKED,
    BOOKED,
    AVAILABLE
}

enum BookingStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}

class Movie {
    private final String id;
    private final String name;

    Movie(String id, String name) {
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

class Theater {
    private final String id;
    private final String name;
    private final List<Screen> screens;

    public Theater(String id, String name, List<Screen> screens) {
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

    public List<Screen> getScreens() {
        return screens;
    }

    public void addScreen(Screen screen) {
        screens.add(screen);
    }
}

class Screen {
    private final String id;
    private final List<Seat> seats;

    public Screen(String id, int totalSeats) {
        this.id = id;
        this.seats = new ArrayList<>();
        for (int i = 1; i <=  totalSeats; i++) {
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
    private final ReentrantLock lock = new ReentrantLock();
    private final String id;
    private SeatStatus status;
    public Seat(String id) {
        this.id = id;
        this.status = SeatStatus.AVAILABLE;
    }

    public boolean tryAcquireSeat() {
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
            if (status == SeatStatus.AVAILABLE) {
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


    public String getId() {
        return id;
    }

    public SeatStatus getStatus() {
        return status;
    }

}

class Show {
    private final String id;
    private final Movie movie;
    private final Screen screen;
    private LocalDateTime localDateTime;
    public Show(String id, Movie movie, Screen screen, LocalDateTime localDateTime) {
        this.id = id;
        this.movie = movie;
        this.screen = screen;
        this.localDateTime = localDateTime;
    }

    public String getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public Screen getScreen() {
        return screen;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}

class Booking {
    private final String id;
    private final User user;
    private final Show show;
    private final List<Seat> seats;
    private BookingStatus bookingStatus;

    public Booking(String id, User user, Show show, List<Seat> seats) {
        this.id = id;
        this.user = user;
        this.show = show;
        this.seats = seats;
        this.bookingStatus = BookingStatus.PENDING;
    }

    public void confirm() {
        bookingStatus = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (bookingStatus == BookingStatus.CONFIRMED) {
            bookingStatus = BookingStatus.CANCELLED;
            seats.forEach(Seat::release);
        }
    }


    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public BookingStatus getStatus() {
        return bookingStatus;
    }
}
class BookingService {
    //Assuming payment is always successful
    private Map<String, Show> shows = new ConcurrentHashMap<>();

    public void addShow (Show show) {
        shows.put(show.getId(), show);
    }

    public List<Movie> browseMovies(Collection<Show> showsList) {
        return showsList.stream().map(Show::getMovie).distinct().toList();
    }

    public Optional<Booking> book(User user, String showId, List<String> seatIds) {
        Show show = shows.get(showId);
        if (show == null) {
            return Optional.empty();
        }

        List<Seat> lockedSeats = new ArrayList<>();
        for (String seatId: seatIds) {
            Seat seat = show.getScreen().getSeats()
                    .stream()
                    .filter(s -> s.getId().equals(seatId))
                    .findFirst()
                    .orElse(null);

            if (seat == null || !seat.tryAcquireSeat()) {
                lockedSeats.forEach(Seat::release);
                return Optional.empty();
            }
            lockedSeats.add(seat);
        }

        //create booking for user
        Booking booking = new Booking(UUID.randomUUID().toString(), user, show, lockedSeats);

        //Assuming payment true
        boolean paymentStatus = true;
        if (paymentStatus) {
            lockedSeats.forEach(Seat::confirm);
            booking.confirm();
            return Optional.of(booking);
        } else {
            lockedSeats.forEach(Seat::release);
            Optional.empty();
        }

        return Optional.empty();
    }
}




public class LLDMovieBooking {
    static void main(String args[]) {

        User alisceUser = new User("U1", "Alice");

        Movie movie = new Movie("M1", "Movie 3 Idiots");
        Screen screen = new Screen("S1", 10);

        Show show = new Show(UUID.randomUUID().toString(), movie, screen, LocalDateTime.now());

        BookingService bookingService = new BookingService();
        bookingService.addShow(show);

        Optional<Booking> booking = bookingService.book(alisceUser, show.getId(), Arrays.asList("S3", "S4"));

        booking.ifPresentOrElse(b -> System.out.println("Booking confirmed: "+ b.getId()) , () -> System.out.println("Booking Failed!"));


    }
}

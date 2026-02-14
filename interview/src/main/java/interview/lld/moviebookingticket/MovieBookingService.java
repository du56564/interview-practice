package interview.lld.moviebookingticket;


import interview.lld.moviebookingticket.models.*;
import interview.lld.moviebookingticket.strategy.pricing.PricingStrategy;
import interview.lld.moviebookingticket.strategy.payment.PaymentStrategy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// Singleton
public class MovieBookingService {
    private static MovieBookingService INSTANCE = new MovieBookingService();

    private final Map<String, City> cities;
    private final Map<String, Cinema> cinemas;
    private final Map<String, Movie> movies;
    private final Map<String, User> users;
    private final Map<String, Show> shows;

    private final BookingManager bookingManager;
    private final SeatLockManager seatLockManager;

    private MovieBookingService () {
        this.cities = new ConcurrentHashMap<>();
        this.cinemas = new ConcurrentHashMap<>();
        this.movies = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
        this.shows = new ConcurrentHashMap<>();
        this.seatLockManager = new SeatLockManager();
        this.bookingManager = new BookingManager(seatLockManager);

    }

    public static MovieBookingService getInstance() {
        if (INSTANCE == null) {
            synchronized (MovieBookingService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MovieBookingService();
                }
            }
        }
        return INSTANCE;
    }

    public City addCity(String id, String name) {
        City city = new City(id, name);
        cities.put(city.getId(), city);
        return city;
    }

    public Movie addMovie(Movie movie) {
        movies.put(movie.getId(), movie);
        return movie;
    }


    public Cinema addCinema(String id, String name, String cityId, List<Screen> screens) {
        City city = cities.get(cityId);
        Cinema cinema = new Cinema(id, name, city, screens);
        cinemas.put(id, cinema);
        return cinema;
    }

    public Show addShow(String id, Movie movie, Screen screen, LocalDateTime startTime, PricingStrategy pricingStrategy) {
        Show show = new Show(id, movie, screen, startTime, pricingStrategy);
        shows.put(show.getId(), show);
        return show;
    }


    public User createUser(String name, String email) {
        User user = new User(name, email);
        users.put(user.getId(), user);
        return user;
    }


    public List<Show> findShows(String movieTitle, String cityName) {
        List<Show> result = new ArrayList<>();
        shows.values().stream()
                .filter(show -> show.getMovie().getTitle().equalsIgnoreCase(movieTitle))
                .filter(show -> {
                    Cinema cinema = findCinemaForShow(show);
                    boolean val = cinema != null && cinema.getCity().getName().equalsIgnoreCase(cityName);
                    System.out.println(val);
                    return val;
                })
                .forEach(result::add);
        return result;
    }

    private Cinema findCinemaForShow(Show show) {
        return cinemas.values().stream()
                .filter(cinema -> cinema.getScreens().contains(show.getScreen()))
                .findFirst()
                .orElse(null);
    }

    public Optional<Booking> bookTickets(String userId, String showId, List<Seat> desiredSeats, PaymentStrategy paymentStrategy) {
        return bookingManager.createBooking(
                users.get(userId),
                shows.get(showId),
                desiredSeats,
                paymentStrategy
        );
    }

}

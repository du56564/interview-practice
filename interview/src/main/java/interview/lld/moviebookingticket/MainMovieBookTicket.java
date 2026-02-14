package interview.lld.moviebookingticket;

/*
A Movie Ticket Booking System is a software application that enables users to search for movies, view showtimes, select seats,
and book tickets at cinemas or multiplexes.

Requirement
    User? CustomerFacing
    Search, Book seat,

Flow
    Movie -> Cinema -> Time -> Seats -> Pay
    search show based on movie title and city
    support multiple cities, cinemas, screens, and shows.
    support multiple seats type REGULAR, PREMIUM
    multi choice seat booking
    Concurrent
    Avoid double booking
    price dynamic calculation
    subscribe movie


Entity
    - City
    - Cinema
    - Screen
    - Seat
    - Movie
    - Show
    - User
    - Booking
    - SeatLockManager
    - Payment
    - PricingStrategy
        - WeekendPricingStrategy
        - WeekdayPricingStrategy
    - PaymentStrategy
        - CreditCardPaymentStrategy
    - BookingManager
    - MovieBookingService






Design Pattern
    - Composition, Association, Inheritance, Dependency
    - Strategy (PricingStrategy <- WeekdayPricingStrategy, WeekendPricingStrategy , PaymentStrategy <- CreditCardPaymentStrategy)
    - Observer (Movie:Subject, UserObserver: Observer)
    - Builder (Booking.BookingBuilder)
    - Facade (MovieBookingService)
    - Singleton (MovieBookingService)


`
*/

import interview.lld.moviebookingticket.enums.SeatStatus;
import interview.lld.moviebookingticket.enums.SeatType;
import interview.lld.moviebookingticket.models.*;
import interview.lld.moviebookingticket.observer.UserObserver;
import interview.lld.moviebookingticket.strategy.payment.CreditCardPaymentStrategy;
import interview.lld.moviebookingticket.strategy.pricing.WeekdayPricingStrategy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MainMovieBookTicket {
    static void main() {
        MovieBookingService service =  MovieBookingService.getInstance();

        // add city
        City nyc = service.addCity("city1", "New York");
        City la = service.addCity("city2", "Los Angeles");

        // 2. Add movies
        Movie matrix = new Movie("M1", "The Matrix", 120);
        Movie avengers = new Movie("M2", "Avengers: Endgame", 170);
        service.addMovie(matrix);
        service.addMovie(avengers);

        // 3. Add seats for screen
        Screen screen1 = new Screen ("S1");
        for (int i = 1; i <= 10; i++) {
            screen1.addSeat(new Seat("A" + i, 1, i, i <= 5 ? SeatType.REGULAR : SeatType.PREMIUM));
            screen1.addSeat(new Seat("B" + i, 2, i, i <= 5 ? SeatType.REGULAR : SeatType.PREMIUM));
        }

        // Add Cinemas
        Cinema amcNYC = service.addCinema("cinema1", "AMC Times Square", nyc.getId(), List.of(screen1));


        // Add Shows
        Show matrixShow = service.addShow("show1", matrix, screen1, LocalDateTime.now().plusHours(2), new WeekdayPricingStrategy());
        Show avengersShow = service.addShow("show2", avengers, screen1, LocalDateTime.now().plusHours(5), new WeekdayPricingStrategy());

        User alice = service.createUser("Alice", "alice@example.com");
        UserObserver aliceObserver = new UserObserver(alice);
        avengers.addObserver(aliceObserver);

        // Simulate movie release
        System.out.println("\n--- Notifying Observers about Movie Release ---");
        avengers.notifyObservers();

        // --- User Story: Alice books tickets ---
        System.out.println("\n--- Alice's Booking Flow ---");
        String cityName = "New York";
        String movieTitle = "Avengers: Endgame";

        // 1. search show
        List<Show> availableShows = service.findShows(movieTitle, cityName);
        if (availableShows.isEmpty()) {
            System.out.println("No shows found for "+ movieTitle + " in "+ cityName);
            return;
        }
        Show selectedShow = availableShows.get(0);


        //2. view available seats
        List<Seat> availableSeats = selectedShow.getScreen().getSeats().stream()
                                    .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
                                    .collect(Collectors.toList());

        System.out.printf("Available seats for '%s' at %s: %s%n",
                selectedShow.getMovie().getTitle(),
                selectedShow.getStartTime(),
                availableSeats.stream().map(Seat::getId).collect(Collectors.toList()));

        // 3. Select seat
        List<Seat> desiredSeats = List.of(availableSeats.get(2), availableSeats.get(3));
        System.out.println("Alice selects seats: " + desiredSeats.stream().map(Seat::getId).collect(Collectors.toList()));

        // 4. Book Tickets
        Optional<Booking> bookingOpt = service.bookTickets(
                alice.getId(),
                selectedShow.getId(),
                desiredSeats,
                new CreditCardPaymentStrategy("1234-5678-9876-5432", "123"));

        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            System.out.println("\n--- Booking Successful! ---");
            System.out.println("Booking ID: " + booking.getId());
            System.out.println("User: " + booking.getUser().getName());
            System.out.println("Movie: " + booking.getShow().getMovie().getTitle());
            System.out.println("Seats: " + booking.getSeats().stream().map(Seat::getId).collect(Collectors.toList()));
            System.out.println("Total Amount: $" + booking.getTotalAmount());
            System.out.println("Payment Status: " + booking.getPayment().getStatus());
        } else {
            System.out.println("Booking failed.");
        }

        // 5. Verify seat status after booking
        System.out.println("\nSeat status after Alice's booking:");
        desiredSeats.forEach(seat -> System.out.printf("Seat %s status: %s%n", seat.getId(), seat.getStatus()));

    }

}

/*
--- Alice's Booking Flow ---
true
Available seats for 'Avengers: Endgame' at 2026-02-12T04:29:08.318842: [A1, B1, A2, B2, A3, B3, A4, B4, A5, B5, A6, B6, A7, B7, A8, B8, A9, B9, A10, B10]
Alice selects seats: [A2, B2]
Processing credit card payment of $100.00

--- Booking Successful! ---
Booking ID: null
User: Alice
Movie: Avengers: Endgame
Seats: [A2, B2]
Total Amount: $100.0
Payment Status: SUCCESS

Seat status after Alice's booking:
Seat A2 status: BOOKED
Seat B2 status: BOOKED
 */



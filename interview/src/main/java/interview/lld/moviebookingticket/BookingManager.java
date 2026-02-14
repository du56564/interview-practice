package interview.lld.moviebookingticket;


import interview.lld.moviebookingticket.enums.PaymentStatus;
import interview.lld.moviebookingticket.models.*;
import interview.lld.moviebookingticket.strategy.payment.PaymentStrategy;

import java.util.List;
import java.util.Optional;

public class BookingManager {

    private final SeatLockManager seatLockManager;
    public BookingManager (SeatLockManager seatLockManager) {
        this.seatLockManager = seatLockManager;
    }

    public Optional<Booking> createBooking(User user, Show show, List<Seat> seats, PaymentStrategy paymentStrategy) {

        seatLockManager.lockSeats(show, seats, user.getName());

        double totalAmount = show.getPricingStrategy().calculatePrice(seats);

        // Process payment
        Payment payment = paymentStrategy.pay(totalAmount);

        // If payment is successful, create the booking
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            Booking booking = new Booking.BookingBuilder()
                            .setUser(user)
                            .setShow(show)
                            .setSeats(seats)
                            .setTotalAmount(totalAmount)
                            .setPayment(payment)
                            .build();

            // Confirm booking
            booking.confirmBooking();

            seatLockManager.unlockSeats(show, seats, user.getId());

            return Optional.of(booking);
        } else {
            System.out.println("Payment failed. Please try again.");
            return Optional.empty();
        }

    }
}

package interview.lld.moviebookingticket.strategy.payment;

import interview.lld.moviebookingticket.models.Payment;

public interface PaymentStrategy {
    Payment pay(double amount);
}
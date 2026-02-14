package interview.lld.moviebookingticket.strategy.pricing;

import interview.lld.moviebookingticket.models.Seat;

import java.util.List;

public interface PricingStrategy {
    double calculatePrice (List<Seat> seats);
}

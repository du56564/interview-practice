package interview.lld.moviebookingticket.observer;

import interview.lld.moviebookingticket.models.Movie;

public interface MovieObserver {
    void update(Movie movie);
}

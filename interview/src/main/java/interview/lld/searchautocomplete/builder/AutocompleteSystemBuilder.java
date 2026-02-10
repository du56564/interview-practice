package interview.lld.searchautocomplete.builder;

import interview.lld.searchautocomplete.AutocompleteSystem;
import interview.lld.searchautocomplete.strategy.FrequencyBasedRanking;
import interview.lld.searchautocomplete.strategy.RankingStrategy;

// Builder
public class AutocompleteSystemBuilder {
    private RankingStrategy rankingStrategy = new FrequencyBasedRanking(); // Default strategy
    private int maxSuggestions = 10; // Default limit

    public AutocompleteSystemBuilder withRankingStrategy (RankingStrategy strategy) {
        this.rankingStrategy = strategy;
        return this;
    }
    public AutocompleteSystemBuilder withMaxSuggestions (int max) {
        this.maxSuggestions = max;
        return this;
    }

    public AutocompleteSystem build() {
        return new AutocompleteSystem(rankingStrategy, maxSuggestions);
    }


}

package interview.lld.searchautocomplete.strategy;

import interview.lld.searchautocomplete.core.Suggestion;

import java.util.List;
//Strategy
public interface RankingStrategy {
    List<Suggestion> rank (List<Suggestion> suggestions);
}


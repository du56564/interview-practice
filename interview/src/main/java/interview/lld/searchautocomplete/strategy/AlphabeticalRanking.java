package interview.lld.searchautocomplete.strategy;

import interview.lld.searchautocomplete.core.Suggestion;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AlphabeticalRanking implements RankingStrategy{

    @Override
    public List<Suggestion> rank(List<Suggestion> suggestions) {
        return suggestions.stream()
                .sorted(Comparator.comparing(Suggestion::getWord))
                .collect(Collectors.toList());
    }
}

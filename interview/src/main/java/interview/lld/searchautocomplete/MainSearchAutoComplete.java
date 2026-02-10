package interview.lld.searchautocomplete;
/*
1.1 Functional Requirements
    Support inserting words into an internal dictionary.
    Return suggestions when a user types a prefix.
    Suggestions should be ranked based on a configurable strategy (alphabetical or frequency-based).
    The number of suggestions returned should be configurable.
    Frequency count is incremented each time a word is added.
    Words and prefixes are treated case-insensitively.
1.2 Non-Functional Requirements
    The system should be optimized for fast prefix lookups
    The design should follow object-oriented principles with clear separation of concerns
    The system should be modular and extensible to support new ranking strategies
    The components should be testable in isolation
    The system can assume in-memory storage (no persistence required)
*/

/*
Requirements
    search based on partial input

Clarification
    isCaseSensitive No (all input in lower case)
    Character       Only English
    Ranking         alphabetically + frequency of use
    Suggestion limit - configurable, default 10
    Word Dynamic or prelist - Dynamic
    No support for deletion/update only add

Data Structure
    Trie

Entity
    AutoCompleteSystemBuilder (System Class - provided public API)
        AutoCompleteSystem
            RankingStrategy
                FrequencyBasedRanking (Strategy Implementations)
                AlphabeticalRanking (Strategy Implementations)
            Trie root (Core class) (main logic)
                TrieNode (Data Class - hold data)
                Suggestion (Data Class - hold data)

 Design Pattern
    Facade Design (AutoComplete System)
    Composition + Association
    Strategy Pattern (Ranking)
    Builder Pattern (System Construction)
 */


import interview.lld.searchautocomplete.builder.AutocompleteSystemBuilder;
import interview.lld.searchautocomplete.strategy.AlphabeticalRanking;
import interview.lld.searchautocomplete.strategy.FrequencyBasedRanking;

import java.util.List;

public class MainSearchAutoComplete {
    static void main() {

        System.out.println("----------- SCENARIO 1: Frequency-based Ranking -----------");
        AutocompleteSystem systemByFrequency = new AutocompleteSystemBuilder()
                                                    .withMaxSuggestions(5)
                                                    .withRankingStrategy(new FrequencyBasedRanking())
                                                    .build();
        List<String> dictionary = List.of(
                "car", "cat", "cart", "cartoon", "canada", "candy",
                "car", "canada", "canada", "car", "canada", "canopy", "captain"
        );
        systemByFrequency.addWords(dictionary);

        String prefix1 = "ca";
        List<String> suggestions1 = systemByFrequency.getSuggestions(prefix1);
        System.out.println("Suggestions for '" + prefix1 + "': " + suggestions1);


        String prefix2 = "car";
        List<String> suggestions2 = systemByFrequency.getSuggestions(prefix1);
        System.out.println("Suggestions for '" + prefix2 + "': " + suggestions2);


        System.out.println("\n----------- SCENARIO 2: Alphabetical Ranking -----------");
        AutocompleteSystem systemByAlphabetical = new AutocompleteSystemBuilder()
                                                    .withMaxSuggestions(5)
                                                    .withRankingStrategy(new AlphabeticalRanking())
                                                    .build();

        systemByAlphabetical.addWords(dictionary);
        List<String> suggestions3 = systemByAlphabetical.getSuggestions(prefix1);
        System.out.println("Suggestions for '" + prefix1 + "' (alphabetical): " + suggestions3);
    }
}

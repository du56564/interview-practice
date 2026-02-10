package interview.lld.searchautocomplete;

import interview.lld.searchautocomplete.core.Suggestion;
import interview.lld.searchautocomplete.core.Trie;
import interview.lld.searchautocomplete.core.TrieNode;
import interview.lld.searchautocomplete.strategy.RankingStrategy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// Facade
public class AutocompleteSystem {
    private final Trie trie;
    private final RankingStrategy strategy;
    private final int maxSuggestion;

    //Composite
    public AutocompleteSystem (RankingStrategy strategy, int maxSuggestion) {
        this.trie = new Trie();
        this.strategy = strategy;
        this.maxSuggestion = maxSuggestion;
    }

    public void addWord (String word) {
        trie.insert(word.toLowerCase());
    }

    public void addWords (List<String> words) {
        words.forEach(this::addWord);
    }


    /*
        Normalize the prefix to lowercase
        Find the prefix node in the Trie
        If not found, return empty (no matches)
        Collect all words from that node
        Rank them using the configured strategy
        Limit to maxSuggestions
        Extract just the word strings
     */
    public List<String> getSuggestions (String prefix) {
        TrieNode prefixNode = trie.searchPrefix(prefix.toLowerCase());
        if (prefixNode == null) {
            return Collections.emptyList();
        }
        List<Suggestion> rawSuggestion = trie.collectSuggestions(prefixNode, prefix.toLowerCase());
        List<Suggestion> rankedSuggestions = strategy.rank(rawSuggestion);

        return rankedSuggestions.stream()
                .limit(maxSuggestion)
                .map(Suggestion::getWord)
                .collect(Collectors.toList());
    }

}

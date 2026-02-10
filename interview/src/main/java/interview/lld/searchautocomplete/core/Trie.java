package interview.lld.searchautocomplete.core;

import java.util.ArrayList;
import java.util.List;

public class Trie {
    private final TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            if (!current.getChildren().containsKey(ch)) {
                current.getChildren().put(ch, new TrieNode());
            }
            current = current.getChildren().get(ch);
        }
        current.setEndOfWord(true);
        current.incrementFrequency();
    }

    public TrieNode searchPrefix(String prefix) {
        TrieNode current = root;
        for (char ch : prefix.toCharArray()) {
            if (!current.getChildren().containsKey(ch)) {
                return null;
            }
            current = current.getChildren().get(ch);
        }
        return current;
    }

    public List<Suggestion> collectSuggestions(TrieNode startNode, String prefix) {
        List<Suggestion> suggestions = new ArrayList<>();
        collect(startNode, prefix, suggestions);
        return suggestions;
    }

    private void collect(TrieNode node, String currentPrefix, List<Suggestion> suggestions) {
        if (node.isEndOfWord()) {
            suggestions.add(new Suggestion(currentPrefix, node.getFrequency()));
        }

        for (Character ch : node.getChildren().keySet()) {
            collect(node.getChildren().get(ch), currentPrefix + ch, suggestions);
        }
    }
}
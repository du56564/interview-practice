package interview.lld.searchautocomplete.core;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    private Map<Character, TrieNode> children = new HashMap<>();
    private boolean isEndOfWord;
    private int frequency;

    Map<Character, TrieNode> getChildren() {
        return children;
    }

    boolean isEndOfWord() {
        return isEndOfWord;
    }

    int getFrequency() {
        return frequency;
    }

    void setEndOfWord(boolean endOfWord) {
        isEndOfWord = endOfWord;
    }

    void incrementFrequency() {
        this.frequency++;
    }

}

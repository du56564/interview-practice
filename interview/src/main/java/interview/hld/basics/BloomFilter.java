package interview.hld.basics;

import java.util.BitSet;
import java.util.function.Function;
//Bloom Filter acts as a gatekeeper in front of the cache or database
//TC: O(k) K: Ssize of hasFucntion which is 3-10 range constant time so O(1) , SC: O(m) m: bit arraySize
public class BloomFilter {

    private final BitSet bitArray;
    private final int size;
    private final Function<String, Integer>[] hashFunctions;

    public BloomFilter (int size, Function<String, Integer>... hasFunctions) {
        this.bitArray = new BitSet(size);
        this.size = size;
        this.hashFunctions = hasFunctions;
    }

    public void add(String item) {
        for (Function<String, Integer> hasFunction : hashFunctions) {
            int index = Math.abs(hasFunction.apply(item) % size);
            bitArray.set(index);
        }
    }

    public boolean mightPresent(String item) {
        for (Function<String, Integer> hashFunction : hashFunctions) {
            int index = Math.abs(hashFunction.apply(item) % size);
            if (!bitArray.get(index)) {
                return false; // If any bit is 0, the item is definitely not in the set
            }
        }
        return true;
    }
}

class Main {
    static void main(String[] args) {
        // Create a Bloom Filter with 3 simple hash functions
        BloomFilter bloom = new BloomFilter(100,
                s -> s.hashCode(),
                s -> s.length() * 31,
                s -> (s.hashCode() * 17)
        );

        // Add elements
        bloom.add("apple");
        bloom.add("banana");

        // Test membership
        System.out.println("apple? " + bloom.mightPresent("apple"));   // true
        System.out.println("banana? " + bloom.mightPresent("banana")); // true
        System.out.println("grape? " + bloom.mightPresent("grape"));   // maybe false
    }
}

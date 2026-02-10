package interview.lld.bloomfilter;


import interview.lld.bloomfilter.strategy.HashStrategy;

import java.util.BitSet;
import java.util.List;

/*
    bitSet	         BitSet             The bit array storing element presence
    bitSetSize	     int	            Size of the bit array (m)
    numHashFunctions int	            Number of hash functions to use (k)
    hashStrategies	 List<HashStrategy>	Hash function implementations

    add(item)	Adds an element by setting k bits
    mightContain(item)	Returns true if element might be present

    -BitSet bitSet
    -int bitSetSize
    -int numHashFunctions
    -List<HashStrategy> hashStrategies

    +add(String item)
    +mightContain(String item) : boolean

 */
public class BloomFilter {
    private final BitSet bitSet;
    private final int bitSetSize;
    private final int numHashFunctions;
    private final List<HashStrategy> hashStrategies;

    private BloomFilter (int bitSetSize, int numHashFunctions, List<HashStrategy> strategies) {
        this.bitSetSize = bitSetSize;
        this.numHashFunctions = numHashFunctions;
        this.bitSet = new BitSet();
        this.hashStrategies = strategies;
    }

    public synchronized void add (String item) {
        for (int i = 0; i < numHashFunctions; i++) {
            long hash = hashStrategies.get(i).hash(item);
            int index = (int) (Math.abs(hash) % bitSetSize);
            bitSet.set(index);
        }
    }

    public synchronized boolean mightContain (String item) {
        for (int i = 0; i < numHashFunctions; i++) {
            long hash = hashStrategies.get(i).hash(item);
            int index = (int) (Math.abs(hash)  % bitSetSize);
            if (!bitSet.get(index)) {
                return false;
            }
        }
        return true;
    }

    public static class Builder {
        private int bitSetSize;
        private int numHashFunctions;
        private List<HashStrategy> strategies;

        public Builder withBitSetSize(int bitSetSize) {
            if (bitSetSize <= 0) {
                throw new IllegalArgumentException("Bit set size must be positive.");
            }
            this.bitSetSize = bitSetSize;
            return this;
        }

        public Builder withNumHashFunctions(int numHashFunctions) {
            if (numHashFunctions <= 0) {
                throw new IllegalArgumentException("Number of hash functions must be positive.");
            }
            this.numHashFunctions = numHashFunctions;
            return this;
        }

        public Builder withHashStrategies(List<HashStrategy> strategies) {
            if (strategies == null || strategies.isEmpty()) {
                throw new IllegalArgumentException("At least one hash strategy must be provided.");
            }
            this.strategies = strategies;
            return this;
        }

        public BloomFilter build() {
            if (bitSetSize == 0 || numHashFunctions == 0 || strategies == null) {
                throw new IllegalStateException("Must set bit set size, number of hash functions, and strategies.");
            }

            if (strategies.size() < numHashFunctions) {
                throw new IllegalStateException(
                        "The number of provided hash strategies (" + strategies.size() + ") " +
                                "must be at least equal to the number of hash functions required (" + numHashFunctions + ")."
                );
            }

            System.out.printf("Creating Bloom Filter with specified parameters:%n" +
                    "  - Bit set size (m): %d%n" +
                    "  - Hash functions (k): %d%n", bitSetSize, numHashFunctions);

            return new BloomFilter(bitSetSize, numHashFunctions, strategies);
        }
    }
}

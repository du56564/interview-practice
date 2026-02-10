package interview.lld.bloomfilter;

/*

A Bloom Filter is a probabilistic data structure used to test whether an element is a member of a set. It is designed to be very fast
and extremely space-efficient, especially when working with large volumes of data where memory is constrained.

trade-off:
It may produce false positives (saying an element is in the set when it actually isn’t).
But it never produces false negatives (if it says an element is not present, it’s guaranteed to be absent).

Requirements
    Element is String Type support
    Operation: Add & mightContain
    False Positive acceptable, False negative not acceptable
    variable hashFunction (k)[FNV1a, DJB2] and size of bit array (m) be configurable
    Multithreaded. Bloom Filters : add(element) and mightContain(element) multiple threads.
    Idempotent operation TC for both fxn: O(1)

Core Entities
    1. Bit Array (Data Store) - add(elm), mightContain(elm)
        init all bit to 0
        add(elem) - multiple hash(k)  bits, bits set to 1
        mightContain(elem) - if all are 1, element might be in the set, if any bit 0 then definitely not present.
    2. Hash Function - multiple k hash fxn
            HashType - enum FNV1A, DJB2
            HashStrategy
            Concrete Strategies ->  FNV1aHashStrategy, DJB2HashStrategy
    3. BloomFilter - should be configurable and thread-safe
       bitArray
       reference hash fxn
       add, mightContain
       handle thread safety

Design Pattern
    Strategy Pattern
    Factory Pattern
    Builder Pattern


 */

import interview.lld.bloomfilter.enums.HashType;
import interview.lld.bloomfilter.factory.HashStrategyFactory;
import interview.lld.bloomfilter.strategy.HashStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainBloomFilter {

    static void main() {
        int bitSetSize = 10000;
        int numHashFunctions = 2;
        int expectedInsertions = 1000;

        List<HashStrategy> strategies = List.of(HashStrategyFactory.createFactory(HashType.FNV1A)
                , HashStrategyFactory.createFactory(HashType.DJB2));

        BloomFilter filter = new BloomFilter.Builder()
                .withBitSetSize(bitSetSize)
                .withNumHashFunctions(numHashFunctions)
                .withHashStrategies(strategies)
                .build();

        // --- Add elements to the filter ---
        System.out.println("\n--- Adding elements to the filter ---");
        List<String> insertedElements = new ArrayList<>();
        for (int i = 0; i < expectedInsertions; i++) {
            String element = "user" + i + "@example.com";
            insertedElements.add(element);
            filter.add(element);
        }
        System.out.println(expectedInsertions + " elements have been added.");

        // --- Test for presence (no false negatives) ---
        // --- 5. Test for presence (no false negatives) ---
        System.out.println("\n--- Verifying no false negatives ---");
        boolean hasFalseNegatives = false;
        for (String element : insertedElements) {
            if (!filter.mightContain(element)) {
                System.err.println("FALSE NEGATIVE DETECTED FOR: " + element);
                hasFalseNegatives = true;
                break;
            }
        }
        if (!hasFalseNegatives) {
            System.out.println("Success! No false negatives found. All inserted elements were detected.");
        }

        // --- 6. Test for false positives ---
        System.out.println("\n--- Testing for false positives ---");
        int testSetSize = 10000;
        int falsePositivesCount = 0;
        for (int i = 0; i < testSetSize; i++) {
            String randomElement = UUID.randomUUID().toString();
            if (filter.mightContain(randomElement)) {
                falsePositivesCount++;
            }
        }
        System.out.println("Number of false positives found: " + falsePositivesCount + " out of " + testSetSize + " random items.");

    }



}




/*
Output:-

/Users/du/Library/Java/JavaVirtualMachines/openjdk-25.0.1/Contents/Home/bin/java -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=53030 -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -classpath /Users/du/IdeaProjects/interview-practice/interview/target/classes interview.lld.bloomfilter.MainBloomFilter
Creating Bloom Filter with specified parameters:
  - Bit set size (m): 10000
  - Hash functions (k): 2

--- Adding elements to the filter ---
1000 elements have been added.

--- Verifying no false negatives ---
Success! No false negatives found. All inserted elements were detected.

--- Testing for false positives ---
Number of false positives found: 329 out of 10000 random items.

Process finished with exit code 0



 */
package interview.corerjava.lambda;
/*
TerminalOperations
forEach, collect, reduce, count, anyMatch, and noneMatch.
 */

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class TerminalOperations {
    static void main() {
        int max = Arrays.stream(new int[]{1, 2, 3, 4, 5})
                .reduce(0, Integer::max); // Sums up all elements
        System.out.println(max);

        // count
        long count = Arrays.stream(new int[]{1, 2, 3, 4, 5}).count();
        System.out.println(count);

        // match
        boolean hasEven = Arrays.stream(new int[]{1, 3, 5, 2})
                .anyMatch(n -> n % 2 == 0); // Checks if any number is even

        System.out.println(hasEven); // Prints: true

        boolean allOdd = Arrays.stream(new int[]{1, 3, 5, 7})
                .noneMatch(n -> n % 2 == 0);




    }
}

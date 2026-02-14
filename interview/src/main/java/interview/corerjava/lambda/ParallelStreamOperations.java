package interview.corerjava.lambda;

import java.util.Arrays;
import java.util.List;

/*
A parallel stream is a special type of stream that allows for concurrent processing of elements.
When you convert a stream into a parallel stream.
useful for CPU-bound tasks where you can benefit from the multi-threaded capabilities of hardware.
 */
public class ParallelStreamOperations {
    static void main() {

        List<Integer> numbers1 = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        // Using parallel stream to calculate the sum
        int sum = numbers1.parallelStream()
                .mapToInt(Integer::intValue)
                .sum();
        System.out.println("Sum: " + sum); // Output: 55

        numbers1.parallelStream()
                .forEach(item -> {
                    System.out.println(Thread.currentThread().getName() + ": " + item);
                });

        //performance : sequential vs parallel
        List<Integer> numbers = generateLargeList(10);
        System.out.println(numbers);
        long startTime = System.currentTimeMillis();
        int sumSequential = numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
        long sequentialTime = System.currentTimeMillis() - startTime;
        startTime = System.currentTimeMillis();
        int sumParallel = numbers.parallelStream()
                .mapToInt(Integer::intValue)
                .sum();
        long parallelTime = System.currentTimeMillis() - startTime;

        System.out.println("Sequential Sum: " + sumSequential + " Time: " + sequentialTime + " ms");
        System.out.println("Parallel Sum: " + sumParallel + " Time: " + parallelTime + " ms");



    }
    private static List<Integer> generateLargeList(int size) {
        return Arrays.asList(new Integer[size]);
    }
}



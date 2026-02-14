package interview.corerjava.lambda;

/*
Stream is a sequence of elements from a source, which can be processed in a functional style.
*/

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamBasics {
    static void main() {

        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");

        // Creating a stream from a list
        names.stream().forEach(System.out::println);


        //From an Array
        String[] colors = {"red", "green", "blue"};
        Stream<String> colorsStream = Arrays.stream(colors);;
        colorsStream.forEach(System.out::println);

        //Stream builder
        Stream<String> customStream = Stream.<String> builder()
                .add("Apple")
                .add("Banana")
                .add("Cherry")
                .build();
        customStream.forEach(System.out::println);


        //Stream operation:-
        // Intermediate Operations : filter(), map(), distinct(), sorted()
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);
        numbers.stream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .forEach(System.out::println);

        // Terminal Operations : forEach(), collect(), reduce(), count()
        List<String> upperCaseNames = names.stream()
                .map(String::toUpperCase) // Convert to uppercase
                .collect(Collectors.toList()); // Collect back to a List
        System.out.println(upperCaseNames);

        // parallel stream
        List<Integer> nums = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        int sum = nums.parallelStream().mapToInt(Integer::intValue).sum();
        System.out.println(sum);

        // Infinite stream : be carful
        Stream<Integer> fibStream = Stream.iterate(new int[]{0,1}, fib -> new int[] {fib[1], fib[0]+fib[1]})
                .map(fib -> fib[0])
                .limit(10);
        fibStream.forEach(System.out::println);



    }
}

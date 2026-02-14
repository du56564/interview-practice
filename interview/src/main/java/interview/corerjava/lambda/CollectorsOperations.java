package interview.corerjava.lambda;

/*
provide a way to transform the results of a stream into a different form, most commonly a collection such as a List, Set, or Map.
 They serve as the bridge between the stream processing domain and the collections framework.
 */

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectorsOperations {
    static void main() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana");

        // Collecting names into a list
        List<String> collectedNames = names.stream()
                .filter(name -> name.startsWith("A"))
                .collect(Collectors.toList());

        System.out.println(collectedNames);



        Map<String, Integer> nameLengthMap = names.stream()
                .collect(
                        Collectors.toMap(name -> name, String::length));
        System.out.println(nameLengthMap);

        //grouping
        Map<Integer, List<String>> groupedByLength = names.stream()
                .collect(Collectors.groupingBy(String::length));

        System.out.println(groupedByLength); // Output: {3=[Bob], 4=[Ella], 5=[Alice, David], 7=[Charlie]}

        //nested grouping
        Map<Integer, Map<Character, List<String>>> grouped = names.stream()
                .collect(Collectors.groupingBy(String::length,
                        Collectors.groupingBy(name -> name.charAt(0))));

        System.out.println(grouped); // Output: {3={B=[Bob]}, 4={E=[Ella]}, 5={A=[Alice], D=[David]}, 7={C=[Charlie]}}

        //partiioned by : splitting data into two groups
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6);

        Map<Boolean, List<Integer>> partitioned = numbers.stream()
                .collect(Collectors.partitioningBy(num -> num % 2 == 0));

        System.out.println(partitioned); // Output: {false=[1, 3, 5], true=[2, 4, 6]}
    }


}

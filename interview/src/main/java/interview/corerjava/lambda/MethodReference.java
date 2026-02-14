package interview.corerjava.lambda;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/*
method reference is a shorthand notation of a lambda expression that executes just one method.

 */
public class MethodReference {
    static void main() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

        Collections.sort(names, String::compareTo);
        System.out.println(names);

        //reference static method
        List<String> words = Arrays.asList("hello", "world", "java");
        List<String> uppercase = words.stream().map(String::toUpperCase).collect(Collectors.toList());
        System.out.println(uppercase);

        // reference instnace method
        Printer printer = new Printer();
        List<String> messages = Arrays.asList("Hello", "World");
        messages.forEach(Printer::print);

        names.sort(String::compareToIgnoreCase);

        //reference to constructor
        List<Person> people = names.stream().map(Person::new).collect(Collectors.toList());
        people.forEach(p -> System.out.println(p));

        List<Integer> lengths = words.stream().map(String::length).collect(Collectors.toList());
        System.out.println(lengths);

        //BiFunction<String, String, Integer> compareFunction = String::compareTo;


    }

    public static String toUpperCase(String input) {
        return input.toUpperCase();
    }
}
class Printer {
    public static void print(String message) {
        System.out.println(message);
    }
}

class Person {
    private String name;

    public Person(String name) {
        this.name = name;
    }
}

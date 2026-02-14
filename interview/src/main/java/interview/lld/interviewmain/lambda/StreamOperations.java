package interview.lld.interviewmain.lambda;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/*
stream operations can be divided into two categories: intermediate operations and terminal operations.
 */
public class StreamOperations {
    static void main() {
        //filter
        List<String> names = List.of("Alice", "Bob", "Charlie", "David", "Eve");
        List<String> filteredNames = names.stream().filter(name -> name.startsWith("A"))
                .collect(Collectors.toList());
        System.out.println(filteredNames);

        // anyMatch, allMatch, noneMatch
        List<Integer> numbers = List.of(1, 2, 3, 4, 5);
        boolean hasEven = numbers.stream().anyMatch(n -> n % 2 == 0);
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);

        System.out.println(hasEven);       // Output: true
        System.out.println(allPositive);    // Output: true
        System.out.println(noneNegative);   // Output: true

        // map - convert element from one form to another
        List<String> words = List.of("Java", "Stream", "Operations");
        List<Integer> lengths = words.stream().map(String::length).collect(Collectors.toList());
        System.out.println(lengths);

        //Chaining Operations
        List<Integer> filteredLengths = names.stream()
                .filter(name -> name.length() > 3) // Filter names longer than 3 characters
                .map(String::length)                // Map to their lengths
                .collect(Collectors.toList());
        System.out.println(filteredLengths); // Output: [5, 7]


        // Reducing data - stream to single result
        int sum = numbers.stream()
                .reduce(0, Integer::sum);
        System.out.println(sum); // Output: 15

        List<String> namesOne = List.of("Alice", "Bob", "Charlie", "David", "Eve");
        Set<String> uniqueNames = namesOne.stream().collect(Collectors.toSet());
        System.out.println(uniqueNames);

        // Example : Data processing
        List<Transaction> transactions = List.of(
                new Transaction("Electronics", 199.99),
                new Transaction("Books", 29.99),
                new Transaction("Electronics", 499.99)
        );
        double sumAmount = transactions.stream()
                .filter(t -> "Electronics".equalsIgnoreCase(t.getCategory()))
                .mapToDouble(t -> t.getAmount())
                .sum();
        System.out.println(sumAmount);

        List<Customer> customers = List.of(
                new Customer("Alice", 25, "USA"),
                new Customer("Bob", 30, "UK"),
                new Customer("Charlie", 35, "USA"),
                new Customer("David", 40, "Canada")
        );
        Map<String, Long> customerCountByCountry = customers.stream()
                .collect(Collectors.groupingBy(Customer::getCountry, Collectors.counting()));
        System.out.println(customerCountByCountry);


    }
}


class Transaction {
    private String category;
    private double amount;

    public Transaction(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public double getAmount() {
        return amount;
    }
}

class Customer {
    private String name;
    private int age;
    private String country;

    public Customer(String name, int age, String country) {
        this.name = name;
        this.age = age;
        this.country = country;
    }

    public String getCountry() {
        return country;
    }
}

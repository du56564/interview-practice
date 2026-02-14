package interview.corerjava.lambda;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/*
IntermediateOperation
filter()
map()
flatMap()
distinct()
sorted()
peek()

 */
public class IntermediateOperations {
    static void main() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David", "Edward");

        // Filter names that start with 'A'
        List<String> filteredNames = names.stream()
                .filter(name -> name.startsWith("A"))
                .toList();

        System.out.println(filteredNames); // Output: [Alice]

        List<Order> orders = Arrays.asList(
                new Order("Laptop", 1200.00),
                new Order("Phone", 800.00),
                new Order("Tablet", 300.00)
        );

        // Filter orders greater than $500
        List<Order> expensiveOrders = orders.stream()
                .filter(order -> order.getPrice() > 500)
                .toList();

        System.out.println("Expensive Orders: " + expensiveOrders.size());


        //map
        // Convert names to uppercase
        List<String> upperCaseNames = names.stream()
                .map(String::toUpperCase)
                .toList();

        System.out.println(upperCaseNames);

        // flatMap
        List<User> users = Arrays.asList(
                new User("Alice", Arrays.asList("Book1", "Book2")),
                new User("Bob", Arrays.asList("Book3", "Book4"))
        );

        // Flatten all favorite books into a single list
        List<String> allBooks = users.stream()
                .flatMap(user -> user.getFavoriteBooks().stream())
                .toList();
        System.out.println("All Favorite Books: " + allBooks);

        // Get distinct names
        List<String> distinctNames = names.stream()
                .distinct()
                .toList();

        System.out.println(distinctNames);

        List<MyUser> myusers = Arrays.asList(
                new MyUser("bob@example.com"),
                new MyUser("alice@example.com"),
                new MyUser("charlie@example.com")
        );
        List<String> sortedEmails = myusers.stream().map(MyUser::getEmail).sorted(Comparator.reverseOrder()).toList();
        System.out.println(sortedEmails);



    }
}

class Order {
    private String product;
    private double price;

    public Order(String product, double price) {
        this.product = product;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}

class User {
    private String name;
    private List<String> favoriteBooks;

    public User(String name, List<String> favoriteBooks) {
        this.name = name;
        this.favoriteBooks = favoriteBooks;
    }

    public List<String> getFavoriteBooks() {
        return favoriteBooks;
    }
}


class MyUser {
    private String email;

    public MyUser(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
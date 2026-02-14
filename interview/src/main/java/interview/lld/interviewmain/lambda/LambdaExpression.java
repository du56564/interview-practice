package interview.lld.interviewmain.lambda;
/*
Predicate<T>: Represents a boolean-valued function of one argument.
Consumer<T>: Represents an operation that accepts a single input argument and returns no result.
Supplier<T>: Represents a supplier of results.
Function<T, R>: Represents a function that accepts one argument and produces a result.
 */


import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class LambdaExpression {
    static void main() {

        Predicate<String> isEmpty = str -> str.isEmpty();
        System.out.println(isEmpty);

        List<String> names = new ArrayList<>();
        names.add("Tom");
        names.add("Alice");
        names.add("Bob");


        names.sort((s1, s2) -> s2.length() - s1.length());
        //names.sort(Comparator.comparingInt(s -> s.length()));
        System.out.println(names);




    }
}

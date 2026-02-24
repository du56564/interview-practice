package interview.lld.interviewmain;

import java.util.*;

// Define the Student class
class Student {
    String name;
    Integer age;

    // Constructor
    Student(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public Integer getAge() {
        return age;
    }

    // Method to print student details
    @Override
    public String toString() {
        return name + " : " + age;
    }
}


class StudentComparator implements Comparator<Student>{

    public int compare(Student s1, Student s2) {
        int nameCompare = s1.getName().compareTo(s2.getName());
        int ageCompare = s1.getAge().compareTo(s2.getAge());
        return (nameCompare == 0) ? ageCompare : nameCompare;
    }
}

public class ComparatorExample {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();

        students.add(new Student("Ajay", 27));
        students.add(new Student("Sneha", 23));
        students.add(new Student("Simran", 37));

        // Original List
        System.out.println("Original List:");

        // Iterating List
        for (Student it : students) {
            System.out.println(it);
        }

        System.out.println();

        // Sort students by name, then by age
        students.sort(Comparator.comparing(Student::getName).thenComparing(Student::getAge));

        // Sort by name, then by age
        //Collections.sort(students, new StudentComparator());
        //students.forEach(System.out::println);

        // Display message after sorting
        System.out.println("After Sorting:");

        // Iterating using enhanced for-loop after sorting ArrayList
        for (Student it : students) {
            System.out.println(it);
        }
    }
}


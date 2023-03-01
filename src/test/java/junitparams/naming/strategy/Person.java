package junitparams.naming.strategy;

public class Person {
    private String id;
    private String fullName;
    private int age;

    public Person(String id, String fullName, int age) {
        this.id = id;
        this.fullName = fullName;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public int getAge() {
        return age;
    }
}

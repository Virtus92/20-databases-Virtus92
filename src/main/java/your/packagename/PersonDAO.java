package main.java.your.packagename;

public interface PersonDAO {
    int createPerson(String name, int personID) throws RuntimeException;

    void updatePerson(Person person, String name) throws RuntimeException;
    void updatePerson(Person person, int householdID) throws RuntimeException;

    void deletePerson(Person person) throws RuntimeException;

    Person getPerson(int householdId) throws RuntimeException;
}

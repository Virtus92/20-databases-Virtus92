package main.java.your.packagename;

import java.util.HashMap;
import java.util.List;

public class Manager {
    private static final HashMap<Integer, Household> householdsList = new HashMap<>();
    private static final HashMap<Integer, Person> personList = new HashMap<>();
    private static final HashMap<Integer, Pet> petList = new HashMap<>();

    public static HashMap<Integer, Household> getHouseholdsList() {
        return householdsList;
    }
    public static HashMap<Integer, Person> getPersonsList() {
        return personList;
    }
    public static HashMap<Integer, Pet> getPetList() {
        return petList;
    }

    //Households

    public static void addHousehold(Household household) {
        householdsList.put(household.getId(), household);
    }

    public static Household getHousehold(int householdID) {
        return householdsList.get(householdID);
    }

    public static void deleteHousehold(Household household) {
        householdsList.remove(household.getId());
    }

    //Persons

    public static void addPerson(Person person) {
        personList.put(person.getId(), person);
    }

    public static Person getPerson(int personId) {
        return personList.get(personId);
    }

    public static void deletePerson(Person person) {
        personList.remove(person.getId());
    }

    //Pets

    public static void addPet(Pet pet) {
        petList.put(pet.getId(), pet);
    }

    public static Pet getPet(int petID) {
        return petList.get(petID);
    }

    public static void deletePet(Pet pet) {
        petList.remove(pet.getId());
    }

    //Formatierte Ausgabe

    public static String getFormattedPersonsList() {
        return "All Persons: " + personList.values();
    }

    public static String getFormattedHouseholdsList() {
        return "All Households: " + householdsList.values();
    }

    public static String getFormattedPetsList() {
        return "All Pets: " + petList.values();
    }

}

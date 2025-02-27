package main.java.your.packagename;

public interface PetDAO {
    int createPet(String name, int petID) throws RuntimeException;

    void updatePet(Pet pet, String name) throws RuntimeException;
    void updatePet(Pet pet, int personID) throws RuntimeException;

    void deletePet(Pet pet) throws RuntimeException;

    Pet getPet(int petID) throws RuntimeException;
}

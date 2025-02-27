package main.java.your.packagename;

public class Testing {
    public static void testing(String[] args){

//        Delete all
//                System.out.println(Household.getAllHouseholds());
//                List<Household> householdList = Household.getAllHouseholds();
//                for (int i = 0; i < householdList.size(); i++) {
//                    Household.removeHousehold(householdList.get(i));
//                }
//                System.out.println(Household.getAllHouseholds());
//        System.out.println(Manager.getPersonsList() + "\n");

        //Create Test
        System.out.println("\n--- Create Test ---");
        Household family = new Household("Familie 1");
        Person person = new Person("Dinel", family);
        Pet pet = new Pet("Katze", person);

        //Print All
        System.out.println(Manager.getFormattedHouseholdsList());
        System.out.println(Manager.getFormattedPersonsList());
        System.out.println(Manager.getFormattedPetsList());

        //Change Values Test
        family.setName("Familie X");
        person.setName("Kurtovic");
        pet.setName("Hund");

        //After Change - Read items
        System.out.println("\n--- Change Values Test ---\n" + family);
        System.out.println(person);
        System.out.println(pet);

        //Last Overview
        System.out.println("\n--- Last Overview ---");
        System.out.println(Manager.getFormattedHouseholdsList());
        System.out.println(Manager.getFormattedPersonsList());
        System.out.println(Manager.getFormattedPetsList());

        //Delete Test
        System.out.println("\n--- Delete Test ---");
        Pet.deletePet(pet);
        System.out.println("\nPet should be deleted - " + pet);
        System.out.println("List should be empty - " + Manager.getPetList());
        Person.deletePerson(person);
        System.out.println("\nPerson should be deleted - " + person);
        System.out.println("List should be empty - " + Manager.getPersonsList());
        Household.removeHousehold(family);
        System.out.println("\nHousehold should be deleted - " + family);
        System.out.println("List should be empty - " + Manager.getPersonsList());

    }
}

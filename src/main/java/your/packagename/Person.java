package main.java.your.packagename;

public class Person {
    private int id;
    private String name;
    private int householdId;
    private static final PersonDAO personDAO = new PersonDAOImpl();

    //Konstruktor
    public Person(String name, Household household) {
        int householdId = household.getId();
        this.id = personDAO.createPerson(name, householdId);
        this.name = name;
        this.householdId = householdId;
        Manager.addPerson(this);
    }


    //Abrufen
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getHouseholdId() {
        return householdId;
    }

    public Household getHousehold() {
        return Manager.getHousehold(this.householdId);
    }


    //Verändern
    public void setName(String name) {
        personDAO.updatePerson(this, name);
        this.name = name;
    }

    public void setHousehold(Household household) {
        personDAO.updatePerson(this, household.getId());
        this.householdId = household.getId();
    }

    public static Person getPerson(int personID) {
        return personDAO.getPerson(personID);
    }

    //Löschen
    public static void deletePerson(Person person) {
        personDAO.deletePerson(person);
        Manager.deletePerson(person);

        //Setze alle referenzen auf null
        person.id = -1;
        person.name = null;
        person.householdId = -1;

        //Garbage Collection
        person = null;
        System.gc();
    }

    @Override
    public String toString() {
        if (this.id == -1) {
            return "Person wurde gelöscht.";
        }

        Household household = Household.getHousehold(this.householdId);
        if (household == null) {
            return "Person {" + id + " = " + name + ", Household = unbekannt}";
        }


        return "Person {" + id + " = " + name + ", Household = " + household.getId() + " = " + household.getName() + "}";
    }
}

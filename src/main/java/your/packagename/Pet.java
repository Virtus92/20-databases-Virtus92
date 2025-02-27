package main.java.your.packagename;

public class Pet {
    private int id;
    private String name;
    private int personID;
    private static final PetDAO petDAO = new PetDAOImpl();

    //Konstruktor
    public Pet(String name, Person person) {
        this.id = petDAO.createPet(name, person.getId());
        this.name = name;
        this.personID = person.getId();
        Manager.addPet(this);
    }

    //Abrufen
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public Person getPerson() {
        return Manager.getPerson(personID);
    }
    public int getPersonID() {
        return personID;
    }

    public static Pet getPet(int petID) {
        return petDAO.getPet(petID);
    }

    //Verändern
    public void setName(String name) {
        petDAO.updatePet(this, name);
        this.name = name;
    }
    public void setPerson(Person person) {
        petDAO.updatePet(this, personID);
        this.personID = person.getId();
    }

    //Löschen
    public static void deletePet(Pet pet) {
        petDAO.deletePet(pet);
        Manager.deletePet(pet);

        //Setze alle referenzen auf null
        pet.id = -1;
        pet.name = null;
        pet.personID = -1;

        //Garbage Collection
        pet = null;
        System.gc();
    }

    @Override
    public String toString() {
        if (this.id == -1) {
            return "Pet wurde gelöscht.";
        }

        Person person = Person.getPerson(this.personID);
        if (person == null) {
            return "Pet {" + id + " = " + name + ", Besitzer = unbekannt}";
        }

        return "Pet {" + id + " = " + name + ", Besitzer = " + person.getId() + " = " + person.getName() + "}";
    }
}

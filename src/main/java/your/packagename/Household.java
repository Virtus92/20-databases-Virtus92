package main.java.your.packagename;

import java.util.List;

public class Household {
    private int id;
    private String name;
    private static final HouseholdDAO householdDAO = new HouseholdDAOImpl();


    //Konstruktoren
    public Household(String name) {
        this.id = householdDAO.createHousehold(name);
        this.name = name;
        Manager.addHousehold(this);
    }

    public Household(int id, String name) {
        this.name = name;
        this.id = id;
        Manager.addHousehold(this);
    }


    //Abrufen
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Household getHousehold(int householdID) {
        return householdDAO.getHousehold(householdID);
    }

    public static List<Household> getAllHouseholds() {
        return householdDAO.getAllHouseholds();
    }


    //Verändern
    public void setName(String name) {
        householdDAO.updateHousehold(this, name);
        this.name = name;
    }


    //Löschen
    public static void removeHousehold(Household household) {
        householdDAO.deleteHousehold(household);
        Manager.deleteHousehold(household);

        //Setze alle referenzen auf null
        household.id = -1;
        household.name = null;

        //Garbage Collection
        household = null;
        System.gc();
    }


    @Override
    public String toString() {
        if (this.id == -1) {
            return "Haushalt wurde gelöscht.";
        }

        return "Household {" + id + " = " + name + "}";
    }
}

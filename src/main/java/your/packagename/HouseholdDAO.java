package main.java.your.packagename;

import java.util.List;

public interface HouseholdDAO {
    int createHousehold(String name) throws RuntimeException;

    void updateHousehold(Household household, String name) throws RuntimeException;

    void deleteHousehold(Household household) throws RuntimeException;

    Household getHousehold(int householdId) throws RuntimeException;
    List<Household> getAllHouseholds() throws RuntimeException;
}

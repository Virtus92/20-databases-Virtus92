package main.java.your.packagename;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class PersonDAOImpl implements PersonDAO {

    @Override
    public int createPerson(String name, int householdID) throws RuntimeException {
        String query = "INSERT INTO Persons (NAME, HOUSEHOLDID) VALUES (?, ?)";

        try (PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setInt(2, householdID);

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Beim INSERT wurde keine Autoincrement-ID generiert");
            }

        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePerson(Person person, String name) throws RuntimeException {
        String query = "UPDATE Persons SET NAME = ? WHERE ID = ?";
        int tempId = person.getId();
        try(PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setInt(2, tempId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePerson(Person person, int householdId) throws RuntimeException {
        String query = "UPDATE Persons SET HOUSEHOLDID = ? WHERE ID = ?";
        int tempId = person.getId();
        try(PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query)) {
            stmt.setInt(1, householdId);
            stmt.setInt(2, tempId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        person.setHousehold(Manager.getHousehold(householdId));
    }


    @Override
    public void deletePerson(Person person) throws RuntimeException {
        String query = "DELETE FROM Persons WHERE ID = ?";
        try(PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query)) {
            int tempID = person.getId();
            stmt.setInt(1, tempID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Person getPerson(int personID) throws RuntimeException {
        String query = "SELECT ID, Name, HouseholdID FROM Persons WHERE ID = ?";
        Person newPerson = null;
        try (PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query)) {
            stmt.setInt(1, personID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                newPerson = Manager.getPerson(personID);
                if (newPerson == null) {
                    newPerson = new Person(rs.getString("NAME"), Manager.getHousehold(rs.getInt("HouseholdID")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return newPerson;
    }
}

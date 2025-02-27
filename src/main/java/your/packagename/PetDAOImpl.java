package main.java.your.packagename;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PetDAOImpl implements PetDAO{

    @Override
    public int createPet(String name, int petID) throws RuntimeException {
        String query = "INSERT INTO Pets (NAME, PersonID) VALUES (?, ?)";

        try (PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setInt(2, petID);

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
    public void updatePet(Pet pet, String name) throws RuntimeException {
        String query = "UPDATE Pets SET NAME = ? WHERE ID = ?";
        int tempId = pet.getId();
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
    public void updatePet(Pet pet, int personID) throws RuntimeException {
        String query = "UPDATE Pets SET PersonID = ? WHERE ID = ?";
        int tempId = pet.getId();
        try(PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query)) {
            stmt.setInt(1, personID);
            stmt.setInt(2, tempId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        pet.setPerson(Manager.getPerson(personID));
    }

    @Override
    public void deletePet(Pet pet) throws RuntimeException {
        String query = "DELETE FROM Pets WHERE ID = ?";
        try(PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query)) {
            int tempID = pet.getId();
            stmt.setInt(1, tempID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Pet getPet(int petID) throws RuntimeException {
        String query = "SELECT ID, Name, PersonID FROM Pets WHERE ID = ?";
        Pet newPet = null;
        try (PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query)) {
            stmt.setInt(1, petID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                newPet = Manager.getPet(petID);
                if (newPet == null) {
                    newPet = new Pet(rs.getString("NAME"), Manager.getPerson(rs.getInt("PersonID")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return newPet;
    }

}

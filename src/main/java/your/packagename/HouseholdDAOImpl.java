package main.java.your.packagename;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class HouseholdDAOImpl implements HouseholdDAO{

    //(query, Statement.RETURN_GENERATED_KEYS)

    @Override
    public int createHousehold(String name) throws RuntimeException {
        String query = "INSERT INTO Households (NAME) VALUES (?)";

        try (PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);

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
    public void updateHousehold(Household household, String name) throws RuntimeException {
        String query = "UPDATE Households SET NAME = ? WHERE ID = ?";
        int tempId = household.getId();
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
    public void deleteHousehold(Household household) throws RuntimeException {
        String query = "DELETE FROM Households WHERE ID = ?";
        try(PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query)) {
            int tempID = household.getId();
            stmt.setInt(1, tempID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Household getHousehold(int householdId) throws RuntimeException {
        String query = "SELECT ID, Name FROM Households WHERE ID = ?";
        Household newHousehold = null;
        try (PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query)) {
            stmt.setInt(1, householdId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                newHousehold = Manager.getHousehold(householdId);
                if (newHousehold == null) {
                    newHousehold = new Household(rs.getInt("ID"), rs.getString("NAME"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return newHousehold;
    }

    @Override
    public List<Household> getAllHouseholds() throws RuntimeException {
        String query = "SELECT ID FROM Households";
        List<Household> tempNewList = new ArrayList<>();
        List<Household> tempSaved = new ArrayList<>(Manager.getHouseholdsList().values());
        try (PreparedStatement stmt = DBConnector.getInstance().prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Household tempHousehold = getHousehold(rs.getInt("ID"));
                tempNewList.add(tempHousehold);
                if(!tempSaved.contains(tempHousehold)) {
                    tempSaved.add(tempHousehold);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return tempNewList;
    }
}

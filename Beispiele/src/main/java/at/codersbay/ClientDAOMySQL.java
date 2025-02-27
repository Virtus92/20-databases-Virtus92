package at.codersbay;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAOMySQL implements ClientDAO{
    @Override
    public List<Client> getClientsByLastname(String lastname) throws RuntimeException {
        ArrayList<Client> clients = new ArrayList<>();
        try (PreparedStatement ps = DBConnector.getInstance().prepareStatement("SELECT ID, FIRSTNAME, LASTNAME, ACTIVE, CREDITLIMIT FROM CLIENTS WHERE LASTNAME LIKE ?")) { // Definition des Statements mit Platzhaltern: '?'
            ps.setString(1, lastname); // Zuweisung der Parameter, hier haben wir lediglich 1 Parameter
            ResultSet rs = ps.executeQuery(); // Absetzen der Query


            // Hier wird das Ergebnis der Abfrage verarbeitet
            while (rs.next()) { // Zugriff auf die nächste Zeile. Wird benötigt, auch wenn das Ergebnis nur 1 Zeile hat!

                // Die Werte werden hier ausschließlich des Verständnisses halber in Variablen gespeichert. Natürlich könnte man die rs.get-Abfragen direkt im "new Client"-Aufruf einsetzen
                int id = rs.getInt("ID");
                String firstName = rs.getString("FIRSTNAME"); // Zugriff auf die Spalte mit dem Namen "FIRSTNAME"
                String lastName = rs.getString("LASTNAME"); // Zugriff auf die Spalte mit dem Namen "LASTNAME"
                boolean active = rs.getBoolean(4); // Zugriff auf die 3. Spalte laut SELECT: ACTIVE
                float creditLimit = rs.getFloat(5); // Zugriff auf die 4. Spalte laut SELECT: CREDITLIMIT

                Client client = new Client(id, firstName, lastName, active, creditLimit);
                Client client2 = new Client(rs.getInt("ID"), rs.getString("FIRSTNAME"), lastName, active, creditLimit);
                clients.add(client);
            }
        } catch (SQLException e) {
            System.err.println("Fehler bei der Datenbankabfrage");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return clients;
    }

    @Override
    public List<Client> getAllClients() throws RuntimeException {
        ArrayList<Client> clients = new ArrayList<>();
        try (PreparedStatement ps = DBConnector.getInstance().prepareStatement("SELECT ID, FIRSTNAME, LASTNAME, ACTIVE, CREDITLIMIT FROM CLIENTS")) { // Definition des Statements mit Platzhaltern: '?'
            ResultSet rs = ps.executeQuery(); // Absetzen der Query


            // Hier wird das Ergebnis der Abfrage verarbeitet
            while (rs.next()) { // Zugriff auf die nächste Zeile. Wird benötigt, auch wenn das Ergebnis nur 1 Zeile hat!

                Client client = new Client(rs.getInt("ID"),
                        rs.getString("FIRSTNAME"),
                        rs.getString("LASTNAME"),
                        rs.getBoolean(4),
                        rs.getFloat(5));
                clients.add(client);
            }
        } catch (SQLException e) {
            System.err.println("Fehler bei der Datenbankabfrage" + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return clients;
    }

    @Override
    public void deactivateClient(Client client) throws RuntimeException {
        Connection connection = null;
        try {
            connection = DBConnector.getInstance();
        }catch(SQLException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        String updateString = "update CLIENTS set ACTIVE = ? where ID = ?";
        String updateStatement = "update CLIENTS set CREDITLIMIT = ? where ID = ?";
        try (PreparedStatement updateActive = connection.prepareStatement(updateString);
             PreparedStatement updateCreditLimit = connection.prepareStatement(updateStatement)){

            // Hier schalten wir den AutoCommit aus, um sicherzustellen, dass unsere beiden Updates im Rahmen einer Transaktion durchgeführt werden und unsere Daten in der Datenbank konsistent bleiben
            DBConnector.getInstance().setAutoCommit(false);

            updateActive.setBoolean(1, false);
            updateActive.setInt(2, client.id);
            updateActive.executeUpdate();

            updateCreditLimit.setFloat(1, 0);
            updateCreditLimit.setInt(2, client.id);
            updateCreditLimit.executeUpdate();
            DBConnector.getInstance().commit();
        }
        catch(SQLException e){
            try {
                connection.rollback();
                e.printStackTrace();
            }finally {
                throw new RuntimeException(e);
            }
        } finally{
            try {
                connection.setAutoCommit(true); // Hier schalten wir den AutoCommit wieder ein.
            }finally {
                throw new RuntimeException("AutoCommit konnte nicht wieder aktiviert werden!");
            }
        }
    }

    @Override
    public int addClient(Client client) throws RuntimeException {
        String addClientString = "INSERT INTO CLIENTS (FIRSTNAME, LASTNAME, ACTIVE, CREDITLIMIT) VALUES (?, ?, ?, ?)";
        int autoIncKeyFromApi = -1;

        try (PreparedStatement addClientStmt = DBConnector.getInstance().prepareStatement(addClientString, Statement.RETURN_GENERATED_KEYS)) {
            addClientStmt.setString(1, client.firstname);
            addClientStmt.setString(2, client.lastname);
            addClientStmt.setBoolean(3, client.active);
            addClientStmt.setFloat(4, client.creditLimit);

            addClientStmt.executeUpdate();

            ResultSet rs = addClientStmt.getGeneratedKeys();
            if (rs.next()) {
                autoIncKeyFromApi = rs.getInt(1); // Jede generierte ID auslesen. In diesem Beispiel wurde nur ein Datenset erstellt.
            } else {
                throw new SQLException("Beim INSERT wurde keine Autoincrement-ID generiert");
            }
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return autoIncKeyFromApi;
    }

    @Override
    public void deleteClient(int id) throws RuntimeException {
        String deleteClientString = "DELETE FROM CLIENTS WHERE ID = ?";
        int autoIncKeyFromApi = -1;

        try (PreparedStatement deleteClientStmt = DBConnector.getInstance().prepareStatement(deleteClientString)) {
            deleteClientStmt.setInt(1, id);

            deleteClientStmt.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}

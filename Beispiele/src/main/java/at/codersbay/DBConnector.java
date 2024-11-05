package at.codersbay;

import java.sql.*;

public class DBConnector{

    private String dbName = "clientmanagement";
    private String username = "root";
    private String password = "";
    private String url = "jdbc:mysql://localhost:3306/" + dbName; //URL zum lokalen MySQL-Server und Datenbank db_name
    private static DBConnector connector = null;
    private Connection connection;

    public DBConnector() throws SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver"); //Driver "com.mysql.cj.jdbc.Driver" laden
            connection = DriverManager.getConnection(url, username, password); // Verbindung mit der Datenbank herstellen und in Variable connection speichern
        } catch (Exception e){
            e.printStackTrace();
            throw new SQLException("Database Connection couldn't be established!", e);
        }
    }

    public static Connection getInstance() throws SQLException {
        if (connector == null){
            connector = new DBConnector();
        } else if (connector.connection.isClosed()){
            connector = new DBConnector();
        }
        return connector.connection;
    }
}

package at.codersbay;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {

        // Zuerst müssen wir eine DAO-Implementierung aussuchen
        ClientDAO clientDAO = new ClientDAOMySQL();
        Client client1 = null;

        try {
            client1 = new Client("Bernfried", "Müller", true, 100000);
            client1.id = clientDAO.addClient(client1);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println();
        System.out.println("Liste der Clients mit dem Nachnamen 'Müller':");

        try {
            for (Client client: clientDAO.getClientsByLastname("Müller")){
                System.out.println(client.toString());
                clientDAO.deactivateClient(client);
                System.out.println(client.id + " wurde deaktiviert");
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println();
        System.out.println("Liste aller Clients nach Deaktivierung:");

        try {
            for (Client client: clientDAO.getAllClients()){
                System.out.println(client.toString());
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println();
        System.out.println("Drücken Sie die Enter-Taste, um die Testeinträge aus der Datenbank zu löschen!");
        new Scanner(System.in).nextLine();

        try{
            for (Client client: clientDAO.getAllClients()){
                clientDAO.deleteClient(client.id);
                System.out.println(client.toString() + " gelöscht!");
            }
        }catch (Exception e){
            System.out.println("Beim Löschen der Clients ist ein Fehler aufgetreten! " + e.getMessage());
        }
    }
}
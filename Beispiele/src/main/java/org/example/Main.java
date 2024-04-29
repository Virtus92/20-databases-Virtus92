package org.example;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException {

        // Zuerst müssen wir eine DAO-Implementierung aussuchen
        ClientDAO clientPersist = new ClientDAOMySQL();
        Client newClient = null;

        try {
            newClient = new Client("Test1", "test", true, 10000);
            newClient.id = clientPersist.addClient(newClient);

            Client newClient2 = new Client("Test2", "test", false, 0);
            newClient2.id = clientPersist.addClient(newClient2);

            Client newClient3 = new Client("Test3", "test3", true, 5000);
            newClient3.id = clientPersist.addClient(newClient3);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println();
        System.out.println("Liste der Clients mit dem Nachnamen 'test':");

        try {
            for (Client client: clientPersist.getClientsByLastname("test")){
                System.out.println(client.toString());
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        try {
            clientPersist.deactivateClient(newClient);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println();
        System.out.println("Liste aller Clients nach Deaktivierung des Clients mit Vornamen 'Test1':");

        try {
            for (Client client: clientPersist.getAllClients()){
                System.out.println(client.toString());
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println();
        System.out.println("Drücken Sie die Enter-Taste, um die Testeinträge aus der Datenbank zu löschen!");
        new Scanner(System.in).nextLine();

        try{
            for (Client client: clientPersist.getAllClients()){
                clientPersist.deleteCLient(client.id);
                System.out.println(client.toString() + " gelöscht!");
            }
        }catch (Exception e){
            System.out.println("Beim Löschen der Clients ist ein Fehler aufgetreten! " + e.getMessage());
        }
    }
}
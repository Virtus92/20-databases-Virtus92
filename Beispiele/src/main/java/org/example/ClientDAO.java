package org.example;

import java.sql.SQLException;
import java.util.List;

public interface ClientDAO{
    public List<Client> getClientsByLastname(String lastname);

    public List<Client> getAllClients();

    public void deactivateClient(Client client) throws SQLException;

    public int addClient(Client client);

    public void deleteCLient(int id);

}

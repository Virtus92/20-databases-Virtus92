package at.codersbay;

import java.sql.SQLException;
import java.util.List;

public interface ClientDAO{
    public List<Client> getClientsByLastname(String lastname) throws RuntimeException;

    public List<Client> getAllClients() throws RuntimeException;

    public void deactivateClient(Client client) throws RuntimeException;

    public int addClient(Client client) throws RuntimeException;

    public void deleteClient(int id) throws RuntimeException;

}

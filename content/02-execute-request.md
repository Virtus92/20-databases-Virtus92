# SQL-Abfrage absetzen

Nachdem wir die Verbindung zum Server (mit Java anhand JDBC) aufgebaut haben, können SQL-Abfragen abgesetzt werden. 

## Abfrage absetzen und Ergebnis verarbeiten

Nachdem wir eine Verbindung mit der Datenbank erstellt, besteht die Arbeit mit Datenbanken aus 2 Phasen:
- eine Abfrage absetzen
- sofern die Abfrage ein Ergebnis liefern soll (typischerweise "SELECT"), wird das Ergebnis durchiteriert, um verarbeitet zu werden.

Stellen wir uns die folgende Abfrage vor:
```
String lastname = "Mair%";
String query = "SELECT Firstname, Lastname, Age, HeightInMeter FROM Client WHERE Lastname LIKE " + lastname;
```

Je nachdem, woher der Wert der Variable lastname herkommt, kann dieser Wert eine Sicherheitslücke darstellen: Gefahr von SQl-Injection.

Z.B. wird der Wert in einem Textfeld in der Applikation eingegeben. Wie wird sichergestellt, dass der:die Benutzer:in nicht versucht mehr Informationen herauszubekommen, indem er:sie statt eines Nachnamen folgendes eingibt: ```'Mair%' OR 1 = 1```. Statt der einen Client würde der gesamte Inhalt der Tabelle ausgegeben werden.

### DAO-Design Pattern

Bevor wir der Datenbank Abfragen schicken müssen wir zuerst dafür sorgen, dass unsere Implementierung austauschbar bleibt bzw. die Flexibilität bietet, auf leichte Art und Weise andere Datenbanken einzubinden.  

Dazu wurde das Design Pattern DAO (Data Access Object) entwickelt. Diese basiert auf das Konzept von Interfaces, welche die Business Logic von der Persistierung (Abfrage des Systems, wo die Daten gespeichert sind bzw. System, das die Daten dauerhaft speichert). In den folgenden Beispielen werden wir diese anwenden.

Die DAO definiert in der Regel für eine Datenbank verschiedene *`CRUD-Methoden`*. In unserem Beispiel könnten wir z.B. folgende Methoden definieren:
- Hinzufügen eines Kunden
- Aktualisieren der Kundendaten
- Löschen eines Kunden anhand seiner ID
- Laden eines Kunden anhand seiner ID
- Suchen eines Kunden anhand von Vor- und Nachname
- ...

[Weitere Beispiele für DAO in Java](https://www.geeksforgeeks.org/data-access-object-pattern/)

### Java

Wie oben angeführt definieren wir zuerst unser Interface:
```java
public interface ClientDAO{
    public List<Client> getClientsByLastname(String lastname);

    public void deactivateClient(Client client);

    public int addClient(Client client);  
}
```

Java bietet zwar auch die Möglichkeit SQL-Requests direkt abzusetzen, allerdings ist dies nur dann sinnvoll, wenn die Abfrage keine variablen Werte beinhaltet.

Im obigen Beispiel ist dies aber nicht der Fall, also besteht eine Sicherheitslücke.

Um diese Gefahr zu entgehen, gibt es in Java eine spezielle Art von Statements: sogenannte *`PreparedStatement`*. Diese bestehen einerseits aus einem `SQL-Statement mit Platzhaltern`, andererseits aus `Parametern`, die gesondert eingefügt werden und dabei auf Code-Injection überprüft werden.

Abgesehen davon, sind *`PreparedStatements`* in der Regel schneller als das oben vorgestellte Pendant.

```java
// Java

import java.security.Permission;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

class ClientDAOMySQL implements ClientDAO {

    public List<Client> getClientsByLastname(String lastname) {
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
                clients.add(client);
            }
        } catch (SQLException e) {
            System.err.println("Fehler bei der Datenbankabfrage");
            e.printStackTrace();
            return null;
        }
        return clients;
    }

    // hier müssen die weiteren Methoden des Interface implementiert
}
```

Hier erkennt man, dass im SQL-Request die einzusetzenden Werte durch ein `?` ersetzt wird, um zu kennzeichnen, dass hier ein Wert eingesetzt werden muss.

Anschließend verwendet man den Befehl *`ps.setString(1, "Mair%")`*, um das Fragezeichen im Request durch einen entsprechenden Wert zu ersetzen. Durch den Befehl *`setString(...)`* wird definiert, dass der Wert ein String sein muss. Der erste Parameter definiert, dass es sich um das erste Fragezeichen handelt, der zweite Parameter ist der einzusetzende Wert. So wird sichergestellt, dass keine Code-Injection erfolgt. 

Für einen String wird *`setString`* verwendet. Die Pendants dazu lauten:
- *`setBoolean`* für einen Boolean.
- *`setInt`* für einen Integer.
- *`setFloat`* für einen Float.
- *`setDouble`* für ein Double.
- *`setDate`* für ein Datum.
- usw.

Nähere Informationen findet man hier: [Oracle Dokumentation: PreparedStatement](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html)

Beim Auslesen des Ergebnisses wird darauf geachtet, dass die richtigen Datentypen ausgelesen werden. Der Objekttyp *`ResultSet`* bietet hierfür eigene Methoden:
- *`getString`*
- *`getInt`*
- *`getBoolean`*
- *`getFloat`*
- *`getDouble`*
- *`getDate`*
- usw.

### C#

In C# ist es ähnlich. Wir definieren zuerst unser Interface:
```c#
public interface IClientDAO
{
    public IList<Client>? GetClientsByLastname(String lastname);

    public void DeactivateClient(Client client);

    public void AddClient(Client client);
}
```


Der benötigte Objekttyp lautet: *`SqlCommand`* in Kombination mit *`SqlParameter`*.

```csharp
using System;
using System.Data.SqlClient;

class ClientDAOMSSQL: IClientDAO
{
    public IList<Client>? GetClientsByLastname(string lastname)
    {
        IList<Client> clients = new List<Client>();
        try
        {
            string query = "SELECT ID, FIRSTNAME, LASTNAME, ACTIVE, CREDITLIMIT FROM CLIENTS WHERE LASTNAME LIKE @Lastname"; // Verwende Platzhalter @Lastname
    
            // Erstelle ein SqlCommand-Objekt mit der Abfrage und der Verbindung
            using (SqlCommand command = new SqlCommand(query, DBConnector.GetInstance()))
            {
                // Füge den Parameter für den Nachnamen hinzu
                command.Parameters.Add(new SqlParameter("@Lastname", System.Data.SqlDbType.VarChar));
                command.Parameters["@Lastname"].Value = lastname;
    
                // Führe die Abfrage aus und erhalte ein SqlDataReader-Objekt
                using (SqlDataReader reader = command.ExecuteReader())
                {
                    // Iteriere durch die Ergebnisdaten und verarbeite sie
                    while (reader.Read())
                    {
                        Client client = new Client(Convert.ToInt32(reader["ID"]),
                                            reader["FIRSTNAME"].ToString(),
                                            reader["LASTNAME"].ToString(),
                                            Convert.ToBoolean(reader["ACTIVE"]),
                                            Convert.ToDouble(reader["CREDITLIMIT"]));
        
    
                        clients.Add(client);
                    }
                }
            }
        }
        catch (SqlException ex)
        {
            Console.WriteLine("Fehler bei der Datenbankabfrage: " + ex.Message);
            return null;
        }
        return clients;
    }
    // hier müssen die weiteren Methoden des Interface implementiert werden
}

```

Die variablen Anteile des Requests werden mit Platzhaltern gekennzeichnet. Diesen fangen mit `@`an. Anschließend wird anhand *`SqlParameter`* definiert, von welchem Datentyp der Wert sein soll. Schließlich wird der Wert zugewiesen.
Siehe:
- [SqlCommand:Dokumentation](https://learn.microsoft.com/de-de/dotnet/api/microsoft.data.sqlclient.sqlcommand?view=sqlclient-dotnet-standard-5.2)
- [SqlParameter: Dokumentation](https://learn.microsoft.com/de-de/dotnet/api/microsoft.data.sqlclient.sqlparameter?view=sqlclient-dotnet-standard-5.2)
- [die verschiedenen SqlDbType: Dokumentation](https://learn.microsoft.com/de-de/dotnet/api/system.data.sqldbtype?view=net-8.0)

In C# erfolgt die Konvertierung der Ergebnisse entweder anhand der *`ToString()`*-Methode oder anhand der Klasse *`Convert`*. Diese bietet verschiedene Konvertierungsmöglichkeiten:
- *`Convert.ToInt32`*
- *`Convert.ToDouble`*
- usw.
Siehe [Convert-Klasse: Dokumentation](https://learn.microsoft.com/de-de/dotnet/api/system.convert?view=net-8.0)


## Ändern von Einträgen

Oben haben wir ein einfaches Beispiel dargelegt: ein *`SELECT`*-Statement. Jetzt wollen wir mehrere *`UPDATE`*-Statements nacheinander ausführen.

Nehmen wir das Beispiel der folgenden Methode, ClientDAOMySQL.updateClient. Diese speichert das Alter der Client in einem ersten Statement und die Größe der Client in einem 2. Statement. Um 

```java
// Java

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;

class ClientDAOMySQL implements ClientDAO{
    public void deactivateClient(Client client) throws SQLException {
        String updateString = "update CLIENTS set ACTIVE = ? where ID = ?";
        String updateStatement = "update CLIENTS set CREDITLIMIT = ? where ID = ?";
        try (PreparedStatement updateActive = DBConnector.getInstance().prepareStatement(updateString);
             PreparedStatement updateCreditLimit = DBConnector.getInstance().prepareStatement(updateStatement)){

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
            DBConnector.getInstance().rollback();
            e.printStackTrace();
        } finally{
            DBConnector.getInstance().setAutoCommit(true); // Hier schalten wir den AutoCommit wieder ein.
        }
    }

    // hier müssen die weiteren Methoden des Interface implementiert werden
}
```

### C#

```csharp
// C# 

using System;
using System.Data.SqlClient;

class ClientDAOMSSQL: IClientDAO
{
    public void DeactivateClient(Client client)
    {
        string updateActiveString = "update CLIENTS set ACTIVE = @Active WHERE ID = @Id";
        string updateCreditLimitString = "update CLIENTS set CREDITLIMIT = @creditLimit WHERE ID = @Id";

        try
        {
            using (SqlConnection connection = DBConnector.GetInstance())
            {
                // Beginne eine Transaktion
                using (SqlTransaction transaction = connection.BeginTransaction())
                {

                    try
                    {
                        // Erstes Update ausführen
                        SqlCommand updateActiveCommand = new SqlCommand(updateActiveString, connection, transaction);
                        updateActiveCommand.Parameters.AddWithValue("@Active", client.Active);
                        updateActiveCommand.Parameters.AddWithValue("@Id", client.Id);
                        int rowsAffected = updateActiveCommand.ExecuteNonQuery();

                        // Zweites Update ausführen
                        SqlCommand updateCreditLimitCommand = new SqlCommand(updateCreditLimitString, connection, transaction);
                        updateCreditLimitCommand.Parameters.AddWithValue("@creditLimit", client.CreditLimit);
                        updateCreditLimitCommand.Parameters.AddWithValue("@Id", client.Id);
                        rowsAffected = updateCreditLimitCommand.ExecuteNonQuery();

                        // Transaktion bestätigen
                        transaction.Commit();
                    }
                    catch (Exception ex)
                    {
                        Console.WriteLine("Fehler beim Aktualisieren des Kunden: " + ex.Message);
                        // Transaktion rückgängig machen
                        transaction.Rollback();
                    }
                }
            }
        }
        catch (Exception e)
        {
            Console.WriteLine("Fehler beim Verbinden mit der Datenbank: " + e.Message);
        }
    }
    // hier müssen die weiteren Methoden des Interface implementiert werden
}
```

## INSERT-Statements und AUTOINCREMENT-IDs

Angenommen unsere Tabelle `CLIENTS` hat folgende Attribute:
- `ID`: AUTOINCREMENT-ID
- `FIRSTNAME`: Vorname
- `LASTNAME`: Nachname
- `ACTIVE`: Ist der Kunde noch aktiv
- `CREDITLIMIT`: Kreditlimit des Kunden

Bei der Anlage eines neuen Eintrags in die Tabelle `CLIENTS` wird die `ID` automatisch von der Datenbank befühlt (`AUTOINCREMENT`). Dadurch ist uns diese `ID` im Programm nicht bekannt. 

### Java

Eine Möglichkeit, diese in Java herauszufinden, besteht darin, die *`RETURN_GENERATED_KEYS`* zu nutzen. Diese werden nach Ausführung des Statements von der Methode *`GeneratedKeys`* abgefragt und in ein `ResultSet` gespeichert. Das `ResultSet` wird auf dieselbe Art und Weise ausgelesen, wie das Ergebnis eines `SELECT`-Requests.

```java
// Java

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

class ClientDAOMySQL implements ClientDAO {
    public int addClient(Client client) {
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
        }
        return autoIncKeyFromApi;
    }

    // hier müssen die weiteren Methoden des Interface implementiert werden
}
```
Hier wird anhand des Parameters `Statement.RETURN_GENERATED_KEYS` definiert, dass das Statement die von der Datenbank automatisch definierte ID zurückgegeben wird. Mit *`addClientStmt.getGeneratedKeys()`* kann die ID anschließend so abgefragt werden, wie mit einem SELECT-Statement.

### C#

```csharp
// C# 

using System;
using System.Data.SqlClient;

class ClientDAOMSSQL: IClientDAO
{
        public int AddClient(Client client)
        {
            // hier wird output INSERTED.ID verwendet, um die ID der eingefügten Client zurückzubekommen (funktioniert aber nur auf MSSQL 2005 und höher) 
            string addClientString = "INSERT INTO CLIENTS (FIRSTNAME, LASTNAME, ACTIVE, CREDITLIMIT) output INSERTED.ID VALUES (@Firstname, @Lastname, @Active, @CreditLimit)";
            int autoIncKeyFromApi = -1;
            try
            {

                using (SqlConnection connection = DBConnector.GetInstance())
                {
                    // Erstelle das SqlCommand-Objekt für das Einfügen des Baristas
                    using (SqlCommand command = new SqlCommand(addClientString, connection))
                    {
                        command.Parameters.AddWithValue("@Firstname", client.Firstname);
                        command.Parameters.AddWithValue("@Lastname", client.Lastname);
                        command.Parameters.AddWithValue("@Active", client.Active);
                        command.Parameters.AddWithValue("@CreditLimit", client.CreditLimit);

                        // Führe das Einfügen aus und erhalte die generierte ID
                        autoIncKeyFromApi = Convert.ToInt32(command.ExecuteScalar());

                        // Verarbeite die generierte ID
                        Console.WriteLine("Die generierte ID lautet: " + autoIncKeyFromApi);
                    }
                }
            }
            catch (SqlException ex)
            {
                Console.WriteLine("Fehler bei der Datenbankabfrage: " + ex.Message);
            }
            catch (Exception e)
            {
                Console.WriteLine("Fehler beim Einfügen der Client in die Datenbank: " + e.Message);
            }
            return autoIncKeyFromApi;
        }
    }
    // hier müssen die weiteren Methoden des Interface implementiert werden
}
```

In C# kann man anhand des SQL-Statements `output INSERTED.ID` ab MSSQL 2005 abfragen, welche ID im Rahmen des INSERT generiert wurde. Mit `command.ExecuteScalar()` liest man das Ergebnis aus. 

Mit älteren Versionen von MSSQL würde man stattdessen folgendes Statement verwenden: `INSERT INTO CLIENTS (FIRSTNAME, LASTNAME, ACTIVE, CREDITLIMIT) VALUES (@Firstname, @Lastname, @Active, @CreditLimit); SELECT SCOPE_IDENTITY()`.

Zurück zur [Startseite](../README.md)

# SQL-Abfrage absetzen

Nachdem wir die Verbindung zum Server (mit Java anhand JDBC) aufgebaut haben, können SQL-Abfragen abgesetzt werden. 

## Abfrage absetzen und Ergebnis verarbeiten

Nachdem wir eine Verbindung mit der Datenbank erstellt, besteht die Arbeit mit Datenbanken aus 2 Phasen:
- eine Abfrage absetzen
- sofern die Abfrage ein Ergebnis liefern soll (typischerweise "SELECT"), wird das Ergebnis durchiteriert, um verarbeitet zu werden.

Stellen wir uns die folgende Abfrage vor:
```
String lastname = "Mair%";
String query = "SELECT Firstname, Lastname, Age, HeightInMeter FROM Person WHERE Lastname LIKE " + lastname;
```

Je nachdem, woher der Wert der Variable lastname herkommt, kann dieser Wert eine Sicherheitslücke darstellen: Gefahr von SQl-Injection.

Z.B. wird der Wert in einem Textfeld in der Applikation eingegeben. Wie wird sichergestellt, dass der:die Benutzer:in nicht versucht mehr Informationen herauszubekommen, indem er:sie statt eines Nachnamen folgendes eingibt: ```'Mair%' OR 1 = 1```. Statt der einen Person würde der gesamte Inhalt der Tabelle ausgegeben werden.

### DAO-Design Pattern

Bevor wir der Datenbank Abfragen schicken müssen wir zuerst dafür sorgen, dass unsere Implementierung austauschbar bleibt bzw. die Flexibilität bietet, auf leichte Art und Weise andere Datenbanken einzubinden.  

Dazu wurde das Design Pattern DAO (Data Access Object) entwickelt. Diese basiert auf das Konzept von Interfaces, welche die Business Logic von der Persistierung (Abfrage des Systems, wo die Daten gespeichert sind bzw. System, das die Daten dauerhaft speichert). In den folgenden Beispielen werden wir diese anwenden.

Die DAO definiert in der Regel für eine Datenbank verschiedene *`CRUD-Methoden`*. In unserem Beispiel könnten wir z.B. folgende Methoden definieren:
- Hinzufügen einer Person
- Aktualisieren einer Person
- Löschen einer Person anhand ihrer ID
- Laden einer Person anhand ihrer ID
- Suchen einer Person anhand von Vor- und Nachname
- ...

[Weitere Beispiele für DAO in Java](https://www.geeksforgeeks.org/data-access-object-pattern/)

### Java

Wie oben angeführt definieren wir zuerst unser Interface:
```java
public interface PersonDAO{
    public void getPersonsByLastname(String lastname);

    public void updateCoffeeSales(HashMap<String, Integer> salesForWeek) throws SQLException;

    public void insertBarista();  
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

class PersonDAOMySQL implements PersonDAO {

    public List<Person> getPersonsByLastname(String lastname) {
        ArrayList<Person> persons = new ArrayList<>();
        try (PreparedStatement ps = DBConnector.getInstance().prepareStatement("SELECT Firstname, Lastname, Age, HeightInMeter FROM Person WHERE Lastname LIKE ?")) { // Definition des Statements mit Platzhaltern: '?'
            ps.setString(1, lastname); // Zuweisung der Parameter, hier haben wir lediglich 1 Parameter
            ResultSet rs = ps.executeQuery(); // Absetzen der Query


            // Hier wird das Ergebnis der Abfrage verarbeitet
            while (rs.next()) { // Zugriff auf die nächste Zeile. Wird benötigt, auch wenn das Ergebnis nur 1 Zeile hat!
                int id = rs.getInt("id");
                String firstName = rs.getString("Firstname"); // Zugriff auf die Spalte mit dem Namen "Firstname"
                String lastName = rs.getString("Lastname"); // Zugriff auf die Spalte mit dem Namen "Lastname"
                int age = rs.getInt(3); // Zugriff auf die 3. Spalte laut SELECT: Age
                float heightInMeter = rs.getFloat(4); // Zugriff auf die 4. Spalte laut SELECT: HeightInMeter

                Person person = new Person(id, firstName, lastName, age, heightInMeter);
                persons.add(person);
            }
        } catch (SQLException e) {
            System.err.println("Fehler bei der Datenbankabfrage");
            e.printStackTrace();
            return null;
        }
        return persons;
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
public interface IPersonDAO{
    public IList<Person> getPersonsByLastname(String lastname);

    public void updateCoffeeSales(HashMap<String, Integer> salesForWeek) throws SQLException;

    public void insertBarista();  
}
```


Der benötigte Objekttyp lautet: *`SqlCommand`* in Kombination mit *`SqlParameter`*.

```csharp
using System;
using System.Data.SqlClient;
using System.Linq;

class PersonDAOMySQL: IPersonDAO
{
    public IList<Person> GetPersonsByLastname(string lastname)
    {
        IList<Person> persons = new List<Person>();
        try
        {
            string query = "SELECT Firstname, Lastname, Age, HeightInMeter FROM Person WHERE Lastname LIKE @Lastname"; // Verwende Platzhalter @Lastname

            // Erstelle ein SqlCommand-Objekt mit der Abfrage und der Verbindung
            using (SqlCommand command = new SqlCommand(query, DbConnector.GetInstance()))
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
                        Person person = new Person(Convert.ToInt32(reader["id"], 
                                            reader["Firstname"].ToString(), 
                                            reader["Lastname"].ToString(), 
                                            Convert.ToInt32(reader["Age"]), 
                                            Convert.ToDouble(reader["HeightInMeter"])

                        persons.Add(person);
                    }
                }
            }
            catch (SqlException ex)
            {
                Console.WriteLine("Fehler bei der Datenbankabfrage: " + ex.Message);
                return null;
            }
        }
    }
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

Oben haben wir ein einfaches Beispiel dargelegt: ein *`SELECT`*-Statement. Jetzt wollen wir mehrere *`Update`*-Statements definieren.

Nehmen wir das Beispiel der folgenden Methode, CoffeesTable.updateCoffeeSales. Diese speichert die Anzahl der in der aktuellen Woche verkauften Pfund Kaffee in der Spalte SALES für jede Kaffeesorte und
aktualisiert die Gesamtzahl der verkauften Pfunde Kaffee in der Spalte TOTAL für jede
Kaffeesorte:

```java
// Java

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;

class PersonDAOMySQL implements PersonDAO{
    public void updateCoffeeSales(HashMap<String, Integer> salesForWeek) throws SQLException {
        String updateString = "update COFFEES set SALES = ? where COF_NAME = ?";
        String updateStatement = "update COFFEES set TOTAL = TOTAL + ? where COF_NAME = ?";
        try (PreparedStatement updateSales = DBConnector.getInstance().prepareStatement(updateString);
            PreparedStatement updateTotal = DBConnector.getInstance().prepareStatement(updateStatement)){
            
            DBConnector.getInstance().setAutoCommit(false); // Hier schalten wir den AutoCommit aus, um sicherzustellen, dass all unsere beiden Updates im Rahmen einer Transaktion durchgeführt werden und unsere Daten in der Datenbank konsistent bleiben

            for (Map.Entry<String, Integer> e : salesForWeek.entrySet()) { // 
                updateSales.setInt(1, e.getValue().intValue());
                updateSales.setString(2, e.getKey());
                int rowsAffected = updateSales.executeUpdate();

                updateTotal.setInt(1, e.getValue().intValue());
                updateTotal.setString(2, e.getKey());
                rowsAffected = updateTotal.executeUpdate(); 
                DBConnector.getInstance().commit();
            }
        }
        catch(SQLException e){
            DBConnector.getInstance().rollback();
        } finally{
            DBConnector.getInstance().setAutoCommit(true); // Hier schalten wir den AutoCommit wieder ein. 
        }
    }

    // hier müssen die weiteren Methoden des Interface implementiert
}
```

```csharp
// C# 

using System;
using System.Collections.Generic;
using System.Data.SqlClient;

class PersonDAOMySQL: IPersonDAO
{
    public void UpdateCoffeeSales(Dictionary<string, int> salesForWeek)
    {
        string updateSalesQuery = "UPDATE COFFEES SET SALES = @Sales WHERE COF_NAME = @CofName";
        string updateTotalQuery = "UPDATE COFFEES SET TOTAL = TOTAL + @Sales WHERE COF_NAME = @CofName";

        using (SqlConnection connection = DBConnector.Getinstance())
        {
            connection.Open();

            // Beginne eine Transaktion
            SqlTransaction transaction = connection.BeginTransaction();

            try
            {
                foreach (KeyValuePair<string, int> entry in salesForWeek)
                {
                    // Erstelle und konfiguriere das SqlCommand-Objekt für das Update der Verkäufe
                    using (SqlCommand updateSalesCommand = new SqlCommand(updateSalesQuery, connection, transaction))
                    {
                        updateSalesCommand.Parameters.Add(new SqlParameter("@Sales"System.Data.SqlDbType.Int, entry.Value));
                        updateSalesCommand.Parameters.Add(new SqlParameter("@CofName"System.Data.SqlDbType.VarChar, entry.Key));

                        // Führe das Update der Verkäufe aus
                        updateSalesCommand.ExecuteNonQuery();
                    }

                    // Erstelle und konfiguriere das SqlCommand-Objekt für das Update des Gesamtverkaufs
                    using (SqlCommand updateTotalCommand = new SqlCommand(updateTotalQuery, connection, transaction))
                    {
                        updateTotalCommand.Parameters.Add(new SqlParameter("@Sales"System.Data.SqlDbType.Int, entry.Value));
                        updateTotalCommand.Parameters.Add(new SqlParameter("@CofName"System.Data.SqlDbType.VarChar, entry.Key));

                        // Führe das Update des Gesamtverkaufs aus
                        updateTotalCommand.ExecuteNonQuery();
                    }
                }

                // Commit der Transaktion, wenn alles erfolgreich war
                transaction.Commit();
            }
            catch (SqlException ex)
            {
                Console.WriteLine("Fehler bei der Datenbankabfrage: " + ex.Message);

                // Rollback der Transaktion bei einem Fehler
                transaction.Rollback();
            }
        }
    }
}
```

## INSERT-Statements und AUTOINCREMENT-IDs

Angenommen unsere Tabelle COFFEES hat folgende Attribute:
- `COF_ID`: AUTOINCREMENT-ID
- `COF_NAME`: Name
- `SALES`: Verkäufe
- `TOTAL`: Summe der Verkäufe

Bei der Anlage eines neuen Eintrags in die Tabelle `COFFEES` wird die `COF_ID` automatisch von der Datenbank befühlt (`AUTOINCREMENT`). Dadurch ist uns diese `COF_ID` im Programm nicht bekannt. Eine Möglichkeit, diese herauszufinden, besteht darin, die *`RETURN_GENERATED_KEYS`* zu nutzen. Diese werden nach Ausführung des Statements von der Methode *`GeneratedKeys`* abgefragt und in ein `ResultSet` gespeichert. Das `ResultSet` wird auf dieselbe Art und Weise ausgelesen, wie das Ergebnis eines `SELECT`-Requests. 

```java
// Java

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.HashMap;

class PersonDAOMySQL implements PersonDAO{
    public void insertBarista(){
      stmt.executeUpdate("INSERT INTO COFFEES (COF_NAME, SALES, TOTAL) VALUES ('BARISTA', 0, 0)", Statement.RETURN_GENERATED_KEYS);

      int autoIncKeyFromApi = -1;
      rs = stmt.getGeneratedKeys();
      if (rs.next()) {
        autoIncKeyFromApi = rs.getInt(1); // Jede generierte ID auslesen. In diesem Beispiel wurde nur ein Datenset erstellt.
      } else {
        throw new SQLException("Beim INSERT wurde keine Autoincrement-ID generiert");
      }       
    }

    // hier müssen die weiteren Methoden des Interface implementiert
}
```
Hier wird anhand des Parameters *`Statement.RETURN_GENERATED_KEYS`* definiert, dass das Statement die von der Datenbank automatisch definierte ID zurückgegeben wird. Mit *`stmt.getGeneratedKeys()`* kann die ID anschließend genauso abgefragt werden, wie mit einem SELECT-Statement.

```csharp
// C# 

using System;
using System.Data.SqlClient;

class PersonDAOMySQL: IPersonDAO
{
    public int InsertBarista()
    {
        string insertQuery = "INSERT INTO COFFEES (COF_NAME, SALES, TOTAL) output INSERTED.ID VALUES ('BARISTA', 0, 0);";
        int autoIncKeyFromApi = -1;
        using (SqlConnection connection = DbConnector.GetInstance())
        {
            // Erstelle das SqlCommand-Objekt für das Einfügen des Baristas
            using (SqlCommand command = new SqlCommand(insertQuery, connection))
            {
                try
                {
                    // Führe das Einfügen aus und erhalte die generierte ID
                    int autoIncKeyFromApi = Convert.ToInt32(command.ExecuteScalar());

                    // Verarbeite die generierte ID
                    Console.WriteLine("Die generierte ID lautet: " + autoIncKeyFromApi);
                }
                catch (SqlException ex)
                {
                    Console.WriteLine("Fehler bei der Datenbankabfrage: " + ex.Message);
                }
            }
        }
        return int autoIncKeyFromApi;
    }
}
```

In C# kann man anhand des SQL-Statements *`output INSERTED.ID`* abfragen, welche ID im Rahmen des INSERTs generiert wurde. Mit *`command.ExecuteScalar()`* liest man das Ergebnis aus.

Zurück zur [Startseite](../README.md)

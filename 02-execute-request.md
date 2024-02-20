# SQL-Abfrage absetzen

Nachdem wir die Verbindung zum Server (mit Java anhand JDBC) aufgebaut haben, können SQL-Abfragen abgesetzt werden. 

## Query direkt absetzen

Ein erster Weg wäre, eine Query direkt abzusetzen. 

### Java

```java
// Java 

String lastname = "Mair%";
String query = "SELECT Firstname, Lastname, Age, HeightInMeter FROM Person WHERE Lastname LIKE " + lastname;
try (Statement stmt = connection.createStatement()) { //zuerst anhand der Connection ein Statement erstellen
    ResultSet rs = stmt.executeQuery(query); // Mit der Statement-Methode executeQuery kann eine Query 1:1 ausgeführt werden
    [...] // hier kommt das Auslesen des Ergebnisses, siehe weiter unten
} catch (SQLException e) {
    System.err.println("Fehler bei der Datenbankabfrage");
    e.printStackTrace();
}
```

### C#

```csharp
// C# 

```

## Prepared Statements absetzen

Je nachdem, woher der Wert der Variable lastname kommt, kann dieser Wert eine Sicherheitslücke darstellen. Z.B. wird der Wert in einem Textfeld in der Applikation eingegeben. Wie wird sichergestellt, dass der:die Benutzer:in nicht versucht mehr Informationen heraus zu bekommen, indem er:sie statt eines Nachnamen folgendes eingibt: ```'Mair%' OR 1 = 1```. Statt der einen Person würde der gesamte Inhalt der Tabelle ausgegeben werden.

Um diese Gefahr zu entgehen gibt es eine spezielle Art von Statements: sogenannte *`PreparedStatement`*. Diese bestehen einerseits aus einem `SQL-Statement mit Platzhaltern`, andererseits aus `Parametern`, die gesondert eingefügt werden und dabei auf Code-Injection überprüft werden.

Abgesehen davon, sind *`PreparedStatements`* in der Regel schneller als das oben vorgestellte Pendant. 

### Java

```java
// Java

import java.sql.PreparedStatement;
import java.sql.ResultSet;

class DbRequests{

    public void getPersonsByLastname(String lastname){
        try (PreparedStatement ps = DBConnector.getInstance().prepareStatement("SELECT Firstname, Lastname, Age, HeightInMeter FROM Person WHERE Lastname LIKE ?");) { // Definition des Statements mit Platzhaltern: '?'
            ps.setString(1, lastname); // Zuweisung der Parameter, hier haben wir lediglich 1 Parameter
            ResultSet rs = ps.executeQuery(); // Absetzen der Query
            [...]
        } catch (SQLException e) {
            System.err.println("Fehler bei der Datenbankabfrage");
            e.printStackTrace();
        }
    }
}
```

Hier erkennt man, dass im SQL-Request die einzusetzenden Werte durch ein `?` ersetzt wird, um kennzuzeichnen, dass hier ein Wert eingesetzt werden muss.

Anschließend verwendet man den Befehl *`ps.setString(1, "Mair%")`*, um das Fragezeichen im Request durch einen entsprechenden Wert zu ersetzen. Durch den Befehl *`setString(...)`* wird definiert, dass der Wert ein String sein muss. Der erste Parameter definiert, dass es sich um das erste Fragezeichen handelt, der zweite Parameter ist der einzusetzende Wert. So wird sichergestellt, dass keine Code-Injection erfolgt. 

Für einen String wird *`setString`* verwendet. Die Pendants dazu lauten:
- *`setBoolean`* für einen Boolean.
- *`setInt`* für einen Integer.
- *`setFloat`* für einen Float.
- *`setDouble`* für einen Double.
- *`setDate`* für ein Datum.
- usw.

Nähere Informationen findet man hier: [Oracle Dokumentation: PreparedStatement](https://docs.oracle.com/javase/8/docs/api/java/sql/PreparedStatement.html)

### C#

```csharp

```

## Ergebnis der Query verarbeiten

Das Ergebnis der Abfrage ist ein sogenanntes `ResultSet`. Man kann es sich wie eine Tabelle vorstellen, die ausgegeben wird. Den `ResultSet` wird folgendermaßen ausgelesen:
- zuerst wird auf die Zeile zugegriffen
- anschließend wird innerhalb der Zeile auf die einzelnen Spalten zugegriffen:
    - anhand des **Spaltenindex** (die Reihenfolge, wird durch den Request vorgegeben, wobei die erste Spalte den Index 1 hat) 
    - anhand des **Spaltennamens** (wie im Request angegeben) auslesen kann.

Wir erweitern jetzt das Beispiel von oben dementsprechend:

### Java

```java
// Java

import java.sql.PreparedStatement;
import java.sql.ResultSet;

class DbRequests{

    public void getPersonsByLastname(String lastname){
        try (PreparedStatement ps = DBConnector.getInstance().prepareStatement("SELECT Firstname, Lastname, Age, HeightInMeter FROM Person WHERE Lastname LIKE ?");) { // Definition des Statements mit Platzhaltern: '?'
            ps.setString(1, lastname); // Zuweisung der Parameter, hier haben wir lediglich 1 Parameter
            ResultSet rs = ps.executeQuery(); // Absetzen der Query
            while (rs.next()){ // Zugriff auf die nächste Zeile. Wird benötigt, auch wenn das Ergebnis nur 1 Zeile hat!
                String firstName = rs.getString("Firstname"); // Zugriff auf die Spalte mit dem Namen "Firstname"
                String lastName = rs.getString("Lastname"); // Zugriff auf die Spalte mit dem Namen "Lastname"
                int age = rs.getInt(3); // Zugriff auf die 3. Spalte laut SELECT: Age
                float heightInMeter = rs.getFloat(4); // Zugriff auf die 4. Spalte laut SELECT: HeightInMeter
                System.out.println(firstName + ", " + lastName + ", Alter: " + age + ", Größe: " + heightInMeter);
            }
        } catch (SQLException e) {
            System.err.println("Fehler bei der Datenbankabfrage");
            e.printStackTrace();
        }
    }
}
```

### C#

```csharp

```

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

class DbRequests{
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
}
```

```csharp
// C# 

```

## INSERT-Statements und AUTOINCREMENT-IDs

Angenommen unsere Tabelle COFFEES hat folgende Attribute:
- `COF_ID`: AUTOINCREMENT-ID
- `COF_NAME`: Name
- `SALES`: Verkäufe
- `TOTAL`: Summe der Verkäufe

Bei der Anlage eines neuen Eintrags in die Tabelle `COFFEES` wird die `COF_ID` automatisch von der Datenbank befühlt (`AUTOINCREMENT`). Dadurch ist uns diese `COF_ID` im Programm nicht bekannt. Eine Möglichkeit, diese herauszufinden, besteht darin, die *`RETURN_GENERATED_KEYS`* zu nutzen. Diese werden  nach Ausführung des Statements von der Methode *`GeneratedKeys`* abgefragt und in ein `ResultSet` gespeichert. Das `ResultSet` wird auf dieselbe Art und Weise ausgelesen, wie das Ergebnis eines `SELECT`-Requests. 

```java
// Java

stmt.executeUpdate("INSERT INTO COFFEES (COF_NAME, SALES, TOTAL) VALUES ('BARISTA', 0, 0)", Statement.RETURN_GENERATED_KEYS);

int autoIncKeyFromApi = -1;
rs = stmt.getGeneratedKeys();
if (rs.next()) {
    autoIncKeyFromApi = rs.getInt(1); // Jede generierte ID auslesen. In diesem Beispiel wurde nur ein Datenset erstellt.
} else {
    throw new SQLException("Beim INSERT wurde keine Autoincrement-ID generiert");
}
```

```csharp
// C# 

```
# Datenbankverbindung

Bevor wir anfangen zu coden, ist es wichtig, zu verstehen, wie die Arbeit mit Datenbanken grundsätzlich funktioniert.

Es gibt durchaus eine gewissen Ähnlichkeit zu den Dateien:
- zuerst muss man sich mit der Datenbank verbinden. 
- anschließend kann man Abfragen absetzen
- am Ende muss man die Verbindung wieder schließen

Das ist doch sehr ähnlich wie bei Dateien, oder?

## Was ist eine Datenbank-Verbindung?

### Die Verbindungsinformationen

Diese kann lokal laufen, aber genauso auf einem anderen Server, aber selbst eine lokale Datenbank verhält sich ähnlich wie eine auf einem fremden Server. Um diese erreichen zu können, muss man zuerst erfahren, wo diese liegt. Man spricht vom **Pfad zur Datenbank**. Dieser besteht aus der **URL zum Datenbankserver** inkl. des entsprechenden **Ports** (MySQL läuft häufig auf Port 3306) und aus dem **Pfad zur Datenbank am Server**.

Mit diesem Pfad kommt man bis zum Server hin, allerdings lässt dieser einen in der Regel noch nicht hinein. Kaum Datenbanken lassen den Zugriff von anonymen Benutzer\*innen zu. Das wäre eine große Sicherheitslücke. Selbst eine interne Datenbank, die vom Internet aus nicht erreichbar ist, sollte vor Angriffen geschützt werden. Die Daten in der Datenbank sind ja in der Regel die Grundlage für das Geschäft. Sie sind sehr viel Wert. Daher ist die Absicherung dieser Daten die höchste Priorität! Der erste Schritt für diesen Schutz ist, den Zugriff aufgrund eines\*er Benutzers\*in auf den Datenbankserver einzuschränken. Weiters sollte auch innerhalb des Datenbankservers der Zugriff spezifisch eingeschränkt werden: sollte User A nur auf die Datenbank X einen Zugriff benötigen, dann bekommt dieser User auch nur für diese eine Datenbank Zugriff.

Dazu benötigen wir entsprechende **Zugangsdaten**. 

### Auswahl der Zugriffsmethode auf die Datenbank

Als nächstes müssen wir uns für eine Methode entscheiden, wie wir mit der Datenbank arbeiten wollen:
- direkt mit dem Protokoll der Datenbank: diesen Weg wählen die wenigsten, da zu kompliziert und sehr spezifisch

![Beispiel Direktzugriff auf Datenbank](img/Beispiel%20mit%20Direktzugriff%20auf%20DB.png)

- mit bestehenden Libraries, die verschiedene Datenbanken unterstützen (z.B. JDBC für Java)

![Beispiel Zugriff über Datenbank-Connector](img/Beispiel%20mit%20JDBC.png)

Als nächstes müssen wir uns für einen Treiber entscheiden. Je Datenbanktyp gibt es entsprechende Treiber. Dieser spielt die Rolle eines Übersetzers von den Standardoperationen in die für diese eine Datenbank spezifische Sprache. In unserem Beispiel verwenden wir eine MySQL-Datenbank. Welchen Treiber wir wählen hängt einerseits von der Programmiersprache ab, andererseits von der anzusprechenden Datenbank. Weiters kann man sich auch die Herangehensweise aussuchen: eine API, um SQL verwenden zu können oder ORM (Object Relationship Mapping), um die Datenbank als Objekt im Programm abzubilden.

### Datenbankverbindung herstellen in verschiedenen Programmiersprachen

Jetzt haben wir alle nötigen Daten, um unsere erste Verbindung zu erstellen. Dies erfolgt folgendermaßen:

### Java

In Java kann man unter mehreren Möglichkeiten auswählen, wie wir mit der Datenbank interagieren wollen. 2 wichtige Möglichkeiten möchten wir hier erwähnen:
- JDBC: ist eine API, um mit der jeweiligen Datenbank (in unserem Beispiel MySQL) zu kommunizieren. Mit JDBC können wir SQL-Abfragen absetzen. Der Hauptvorteil von JDBC ist, dass es am Anfang einfacher ist, zu verstehen. Aus diesem Grund präsentieren wir es hier.
- JPA: ist eine Möglichkeit, die Datenbank in Objekten abzubilden. JPA ist ein Abstraktionslayer, das die Low Level-JDBC-Aufrufe versteckt und dadurch die Arbeit mit Datenbanken auf Dauer vereinfacht. Dies kann dann im Rahmen einer Vertiefung verwendet werden.

Bevor wir den entsprechenden Driver laden können, müssen wir die entsprechende Dependency in das Projekt einbinden. Dies erfolgt in einem Maven-Projekt im *`POM-File`*:

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>[entsprechende Versionsnummer]</version>
</dependency>
```

Anschließend können wir mi der Anbindung der Datenbank anfangen:

```java
import java.sql.*;
import java.io.*;

public class DBConnector{

    private String url = "jdbc:mysql://localhost:3306/db_name"; //URL zum lokalen MySQL-Server und Datenbank db_name
    private String username = "db_name_user";
    private String password = "pw_db_name_user";
    private static Connection connection = null;

    public static Connection getInstance() throws SQLException {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver"); //Driver "com.mysql.cj.jdbc.Driver" laden
            connection = DriverManager.getConnection(url, user, pass); // Verbindung mit der Datenbank herstellen und in Variable connection speichern
        } catch (Exception e){
            e.printStackTrace();
            throw new SQLException("Database Connection couldn't be established!", e);
        }
    }
}
```



### C#

```csharp

```

Zurück zur [Startseite](README.md)

## und was ist, wenn ich die Datenbank nicht mehr brauche?

[TODO Close Database]

Zurück zur [Startseite](README.md)

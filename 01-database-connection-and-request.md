# Datenbankverbindung und -abfrage

Bevor wir anfangen zu coden, holen wir ein Bisschen aus und erklären, wie die Arbeit mit Datenbank grundsätzlich funktioniert.

Es gibt durchaus eine gewissen Ähnlichkeit zu den Dateien:
- zuerst muss man sich mit der Datenbank verbinden
- anschließend kann man sogenannte Requests absetzen
- am Ende muss man die Verbindung wieder schließen

Das ist doch sehr ähnlich wie bei Dateien, oder?

Was ist eine Datenbank-Verbindung?

Die **Verbindung zu einer Datenbank** besteht einerseits aus dem **Pfad zur Datenbank**. Diese kann lokal laufen, aber genauso auf einem anderen Server, aber selbst eine lokale Datenbank verhält sich ähnlich wie eine auf einem fremden Server. Der Pfad besteht aus der **URL zum Datenbankserver** inkl. des entsprechenden **Ports** (MySQL läuft häufig auf Port 3306) und aus dem **Pfad zur Datenbank am Server**. 
Weiters benötigen wir zur Verbindung auch **Zugangsdaten**. Kaum Datenbanken lassen jeden darauf zugreifen. Selbst eine lokale Datenbank, die vom Internet aus nicht erreichbar ist, sollte vor Angriffen geschützt werden. Der erste Schritt für diesen Schutz wäre, dass der Zugriff aufgrund des\*der Benutzers*in eingeschränkt wird.

Zurück zur [Startseite](README.md)

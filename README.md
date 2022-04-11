# opendata-uploadform



## Produktion

Gestartet werden kann das Programm so:

```bash
java -Dspring.profiles.active=production -jar uploadform.jar --server.servlet.context-path=/upload
```

Im Produktivmodus erwartet das Programm folgende Environment-Variablen:

- `DB_FILE` - Datei für die H2-Datenbank
- `DIRECTORY` - in diesem Verzeichnis werden hochgeladene Dateien gespeichert
- `SECRECT` - zufällige Zeichenkette, die zum Erzeugen des HMAC verwendet wird
- `PASSWORD` - Passwort für den (einzigen) Account `leitstelle`

## Entwicklung

Gestartet werden kann das Programm so:

```bash
./mvnw spring-boot:run
```

Im Entwicklungsmodus ist das System vorkonfiguriert: Die Datenbank existiert nur im Speicher und wird nach Beenden des Programms verworfen. Zugangsdaten sind `leitstelle:leitstelle`. Hochgeladene Dateien werden in `/tmp` gespeichert.

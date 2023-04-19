# Splitter

Eine minimalistische Anwendung zur Verwaltung von Ausgaben in Gruppen.

## Installation und Konfiguration

- Download: `git clone https://github.com/Jay1604/splitter`
- GitHub Application
  erstellen: https://docs.github.com/de/apps/oauth-apps/building-oauth-apps/creating-an-oauth-app
- Umgebungsvariablen setzen
- Es kann notwendig sein vor dem ersten Start `docker compose build` auszuführen
- Start der Software `docker compose up`
    - Der Standardport ist 9000

### Umgebungsvariablen

| Name              | Beschreibung                                                               |
|-------------------|----------------------------------------------------------------------------|
| CLIENT_ID         | Client ID zur Authentifizierung mit GitHub OAuth2                          |
| CLIENT_SECRET     | Client Secret zur Authentifizierung mit GitHub OAuth2                      |
| DATABASE_DB       | Name der Datenbank (kann beliebig gewählt werden)                          |
| DATABASE_PASSWORD | Passwort der Datenbank                                                     |
| DATABASE_USERNAME | Nutzername der Datenbank                                                   |
| DATABASE_HOST     | Nur notwendig, wenn gradle verwendet wird <br> dann auf "localhost" setzen |



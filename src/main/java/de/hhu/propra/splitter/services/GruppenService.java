package de.hhu.propra.splitter.services;

import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.domain.models.Person;
import de.hhu.propra.splitter.exceptions.GruppeNotFound;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

@Service
public class GruppenService {

  private Set<Gruppe> gruppen = new HashSet<>();

  private Set<Gruppe> getGruppen() {
    return Set.copyOf(gruppen);
  }

  public Long addGruppe(String gruender, String name) {
    Gruppe gruppe = new Gruppe((long) gruppen.size(), gruender, name);
    gruppen.add(gruppe);
    return gruppe.getId();
  }

  public Set<Gruppe> getGruppenForGithubName(String githubName) {
    return this.getGruppen().stream().filter(
        gruppe -> gruppe.getMitglieder()
            .stream()
            .map(Person::getGitHubName)
            .toList()
            .contains(githubName)
    ).collect(Collectors.toSet());
  }

  public void addUser(String githubName, long gruppenId) {

    Gruppe gruppe = gruppen
        .stream()
        .filter(a -> a.getId().equals(gruppenId))
        .findFirst()
        .orElseThrow(GruppeNotFound::new);

    gruppe.addMitglied(githubName);
  }

  public Gruppe getGruppeForGithubName(String githubName, long id) {
    return this.getGruppen().stream().filter(
        gruppe -> gruppe.getMitglieder()
            .stream()
            .map(Person::getGitHubName)
            .toList()
            .contains(githubName) && gruppe.getId().equals(id)
    ).findFirst().orElseThrow(GruppeNotFound::new);
  }

  public void gruppeschliessen(long gruppenId) {
    getGruppeforGruppenId(gruppenId).setOffen(false);
  }

  private Gruppe getGruppeforGruppenId(long gruppenId) {
    return this.getGruppen().stream().filter(
        gruppe -> gruppe.getId().equals(gruppenId)
    ).findFirst().orElseThrow(GruppeNotFound::new);
  }

  public void addTransaktion(Long gruppenId, String beschreibung, Money betrag, String glaubiger,
      Set<String> schuldner) {
    Gruppe gruppe = getGruppeforGruppenId(gruppenId);
    gruppe.addAusgabe(beschreibung, betrag, glaubiger, schuldner);

  }
}


package de.hhu.propra.splitter.services;

import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.domain.models.Person;
import de.hhu.propra.splitter.exceptions.GruppeNotFoundException;
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

  public void addPersonToGruppe(String githubName, long gruppenId) {

    Gruppe gruppe = gruppen
        .stream()
        .filter(a -> a.getId().equals(gruppenId))
        .findFirst()
        .orElseThrow(GruppeNotFoundException::new);

    gruppe.addMitglied(githubName);
  }

  public Gruppe getGruppeForGithubNameById(String githubName, long id) {
    return this.getGruppen().stream().filter(
        gruppe -> gruppe.getMitglieder()
            .stream()
            .map(Person::getGitHubName)
            .toList()
            .contains(githubName) && gruppe.getId().equals(id)
    ).findFirst().orElseThrow(GruppeNotFoundException::new);
  }

  public void schliesseGruppe(long gruppenId) {
    getGruppeById(gruppenId).setOffen(false);
  }

  public Gruppe getGruppeById(long gruppenId) {
    return this.getGruppen().stream().filter(
        gruppe -> gruppe.getId().equals(gruppenId)
    ).findFirst().orElseThrow(GruppeNotFoundException::new);
  }

  public void addAusgabe(Long gruppenId, String beschreibung, Money betrag, String glaubiger,
      Set<String> schuldner) {
    Gruppe gruppe = getGruppeById(gruppenId);
    gruppe.addAusgabe(beschreibung, betrag, glaubiger, schuldner);

  }
}


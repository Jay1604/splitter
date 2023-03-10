package de.hhu.propra.splitter.services;

import de.hhu.propra.splitter.domain.models.Gruppe;
import de.hhu.propra.splitter.domain.models.Person;
import de.hhu.propra.splitter.exceptions.GruppeNotFoundException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import java.util.stream.Collectors;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

@Service
public class GruppenService {


  private final GruppenRepository gruppenRepository;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public GruppenService(final GruppenRepository gruppenRepository) {
    this.gruppenRepository = gruppenRepository;
  }

  private Set<Gruppe> getGruppen() {
    return gruppenRepository.getGruppen();
  }

  public Long addGruppe(
      String gruender,
      String name
  ) {
    Gruppe gruppe = new Gruppe(
        null,
        gruender,
        name
    );
    return gruppenRepository.addGruppe(gruppe);
  }

  public Set<Gruppe> getGruppenForGithubName(String githubName) {
    return this
        .getGruppen()
        .stream()
        .filter(
            gruppe -> gruppe
                .getMitglieder()
                .stream()
                .map(Person::getGitHubName)
                .toList()
                .contains(githubName)
        )
        .collect(Collectors.toSet());
  }

  public void addPersonToGruppe(
      String githubName,
      long gruppenId
  ) {

    Gruppe gruppe = this
        .getGruppen()
        .stream()
        .filter(a -> a
            .getId()
            .equals(gruppenId))
        .findFirst()
        .orElseThrow(GruppeNotFoundException::new);

    gruppe.addMitglied(githubName);
    gruppenRepository.saveGruppe(gruppe);
  }

  public Gruppe getGruppeForGithubNameById(
      String githubName,
      long id
  ) {
    return this
        .getGruppen()
        .stream()
        .filter(
            gruppe -> gruppe
                .getMitglieder()
                .stream()
                .map(Person::getGitHubName)
                .toList()
                .contains(githubName) && gruppe
                .getId()
                .equals(id)
        )
        .findFirst()
        .orElseThrow(GruppeNotFoundException::new);
  }

  public void schliesseGruppe(long gruppenId) {

    Gruppe gruppeById = getGruppeById(gruppenId);
    gruppeById.setOffen(false);
    gruppenRepository.saveGruppe(gruppeById);
  }

  public Gruppe getGruppeById(long gruppenId) {
    return this
        .getGruppen()
        .stream()
        .filter(
            gruppe -> gruppe
                .getId()
                .equals(gruppenId)
        )
        .findFirst()
        .orElseThrow(GruppeNotFoundException::new);
  }

  public Gruppe getGruppeById(String gruppenId) {
    long id;
    try {
      id = Long.parseLong(gruppenId);
    } catch (NumberFormatException e) {
      throw new GruppeNotFoundException();
    }
    return this.getGruppeById(id);
  }

  public void addAusgabe(
      Long gruppenId,
      String beschreibung,
      Money betrag,
      String glaubiger,
      Set<String> schuldner
  ) {
    Gruppe gruppe = getGruppeById(gruppenId);
    gruppe.addAusgabe(
        beschreibung,
        betrag,
        glaubiger,
        schuldner
    );
    gruppenRepository.saveGruppe(gruppe);

  }
}


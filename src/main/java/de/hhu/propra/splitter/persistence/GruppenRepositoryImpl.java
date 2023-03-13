package de.hhu.propra.splitter.persistence;

import de.hhu.propra.splitter.exceptions.PersonNotFoundException;
import de.hhu.propra.splitter.services.GruppenRepository;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Repository;

@Repository
public class GruppenRepositoryImpl implements GruppenRepository {

  private final SpringDataPersonenRepository personenRepository;
  private final SpringDataGruppenRepository gruppenRepository;

  public GruppenRepositoryImpl(
      SpringDataPersonenRepository personenRepository,
      SpringDataGruppenRepository gruppenRepository
  ) {
    this.personenRepository = personenRepository;
    this.gruppenRepository = gruppenRepository;
  }

  public de.hhu.propra.splitter.domain.models.Gruppe toGruppe(Gruppe gruppeDto) {
    Iterator<Mitglied> listMitglieder = gruppeDto
        .getMitglieder()
        .iterator();
    de.hhu.propra.splitter.domain.models.Gruppe gruppe =
        new de.hhu.propra.splitter.domain.models.Gruppe(
            gruppeDto
                .getId()
                .longValue(),
            getMitgliedName(listMitglieder.next()),
            gruppeDto.getName()
        );
    while (listMitglieder.hasNext()) {
      gruppe.addMitglied(getMitgliedName(listMitglieder.next()));
    }
    for (Ausgabe ausgabe : gruppeDto.getAusgaben()) {
      gruppe.addAusgabe(
          ausgabe.beschreibung(),
          Money
              .of(
                  ausgabe.betrag(),
                  "EUR"
              )
              .divide(100),
          getPersonName(
              ausgabe.glaeubiger()),
          ausgabe
              .schuldner()
              .stream()
              .map(a -> getPersonName(a.person()))
              .collect(Collectors.toSet())
      );
    }
    gruppe.setOffen(gruppeDto.getOffen());
    return gruppe;
  }

  private String getMitgliedName(Mitglied mitglied) {
    return personenRepository
        .findById(mitglied.person())
        .orElseThrow(PersonNotFoundException::new)
        .gitHubName();
  }

  private String getPersonName(int person) {
    return personenRepository
        .findById(person)
        .orElseThrow(PersonNotFoundException::new)
        .gitHubName();
  }

  public Gruppe fromGruppe(de.hhu.propra.splitter.domain.models.Gruppe gruppe) {
    return new Gruppe(
        gruppe
            .getId()
            .intValue(),
        gruppe.getName(),
        gruppe.isOffen(),
        gruppe
            .getMitglieder()
            .stream()
            .map(
                a -> new Mitglied(
                    getOrCreatePersonId(a.getGitHubName()),
                    gruppe
                        .getId()
                        .intValue()
                ))
            .collect(Collectors.toSet()),
        gruppe
            .getAusgaben()
            .stream()
            .map(a -> new Ausgabe(
                null,
                a.getBeschreibung(),
                a
                    .getBetrag()
                    .multiply(100)
                    .getNumber()
                    .longValue(),
                getOrCreatePersonId(a
                    .getGlaeubiger()
                    .getGitHubName()),
                a
                    .getSchuldner()
                    .stream()
                    .map(b -> new Schuldner(getOrCreatePersonId(b.getGitHubName())))
                    .collect(Collectors.toSet())
            ))
            .collect(Collectors.toSet())
    );
  }

  private Integer getOrCreatePersonId(String name) {
    Optional<Person> person = personenRepository.findBygitHubName(name);
    if (person.isPresent()) {
      return person
          .get()
          .id();
    }
    return personenRepository
        .save(new Person(
            null,
            name
        ))
        .id();
  }


  @Override
  public Set<de.hhu.propra.splitter.domain.models.Gruppe> getGruppen() {

    return gruppenRepository
        .findAll()
        .stream()
        .map(this::toGruppe)
        .collect(Collectors.toSet());

  }

  @Override
  public Long addGruppe(de.hhu.propra.splitter.domain.models.Gruppe gruppe) {
    Gruppe gruppeDto = gruppenRepository.save(new Gruppe(gruppe.getName()));
    gruppe.setId(gruppeDto
        .getId()
        .longValue());

    return gruppenRepository
        .save(fromGruppe(gruppe))
        .getId()
        .longValue();
  }

  @Override
  public void saveGruppe(de.hhu.propra.splitter.domain.models.Gruppe gruppe) {
    gruppenRepository.save(fromGruppe(gruppe));
  }

}

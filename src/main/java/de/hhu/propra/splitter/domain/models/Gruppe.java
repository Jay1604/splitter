package de.hhu.propra.splitter.domain.models;


import de.hhu.propra.splitter.exceptions.PersonNotFoundException;
import de.hhu.propra.splitter.stereotypes.AggregateRoot;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.javamoney.moneta.Money;

@AggregateRoot
public class Gruppe implements Comparable<Gruppe> {

  private Long id;
  private final Set<Person> mitglieder;
  private final String name;
  private boolean offen;
  private final Set<Ausgabe> ausgaben;

  public Gruppe(
      Long id,
      String gruender,
      String name
  ) {
    this.id = id;
    this.mitglieder = new HashSet<>(Set.of(new Person(gruender)));
    this.name = name;
    this.offen = true;
    this.ausgaben = new HashSet<>();
  }

  public Set<Person> getMitglieder() {
    return Set.copyOf(mitglieder);
  }

  public String getName() {
    return name;
  }

  public boolean isOffen() {
    return offen;
  }

  public Set<Ausgabe> getAusgaben() {
    return Set.copyOf(ausgaben);
  }

  public void addMitglied(String name) {
    this.mitglieder.add(new Person(name));
  }

  public void addAusgabe(
      String beschreibung,
      Money betrag,
      String glaeubiger,
      Set<String> schuldner
  ) {
    Person glaeubigerPerson = getPersonfromGithubName(glaeubiger);
    Set<Person> schuldnerPersonen = schuldner
        .stream()
        .map(this::getPersonfromGithubName)
        .collect(Collectors.toSet());
    this.ausgaben.add(new Ausgabe(
        beschreibung,
        betrag,
        glaeubigerPerson,
        schuldnerPersonen
    ));
  }

  private Person getPersonfromGithubName(String user) {
    return mitglieder
        .stream()
        .filter(a -> a
            .getGitHubName()
            .equals(user))
        .findFirst()
        .orElseThrow(PersonNotFoundException::new);
  }

  public void setOffen(boolean offen) {
    this.offen = offen;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Gruppe gruppe = (Gruppe) o;

    return id != null ? id.equals(gruppe.id) : gruppe.id == null;
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public int compareTo(Gruppe o) {
    return this.name.compareTo(o.name);
  }
}

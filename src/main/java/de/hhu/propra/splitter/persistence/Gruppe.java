package de.hhu.propra.splitter.persistence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Gruppe {

  @Id
  private Integer id;
  private String name;
  private Boolean offen;
  private Set<Mitglied> mitglieder;
  private Set<Ausgabe> ausgaben;

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Boolean getOffen() {
    return offen;
  }

  public Set<Mitglied> getMitglieder() {
    return Set.copyOf(mitglieder);
  }

  public Set<Ausgabe> getAusgaben() {
    return Set.copyOf(ausgaben);
  }

  @PersistenceCreator
  public Gruppe(Integer id, String name, Boolean offen, Set<Mitglied> mitglieder,
      Set<Ausgabe> ausgaben) {
    this.id = id;
    this.name = name;
    this.offen = offen;
    this.mitglieder = new HashSet<>(mitglieder);
    this.ausgaben = new HashSet<>(ausgaben);
  }

  public Gruppe(String name) {
    this(null, name, true, Set.of(), Set.of());
  }

  @Override
  public String toString() {
    return "Gruppe{"
        + "id=" + id
        + ", name='" + name + '\''
        + ", offen=" + offen
        + ", mitglieder=" + mitglieder
        + ", ausgaben=" + ausgaben
        + '}';
  }

  public void addMitglied(Person person) {
    this.mitglieder.add(new Mitglied(person.id(), this.id));
  }
}

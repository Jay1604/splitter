package de.hhu.propra.splitter.persistence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Gruppe {

  @Id
  Integer id;
  String name;
  Boolean offen;
  Set<Mitglied> mitglieder;
  Set<Ausgabe> ausgaben;

  @PersistenceCreator
  public Gruppe(Integer id, String name, Boolean offen, Set<Mitglied> mitglieder,
      Set<Ausgabe> ausgaben) {
    this.id = id;
    this.name = name;
    this.offen = offen;
    this.mitglieder = mitglieder;
    this.ausgaben = ausgaben;
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
}

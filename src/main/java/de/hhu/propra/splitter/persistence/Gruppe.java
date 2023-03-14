package de.hhu.propra.splitter.persistence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Gruppe {

  @Id
  private final Integer id;
  private final String gruppeName;
  private final Boolean offen;
  private final Set<Mitglied> mitglieder;
  private final Set<Ausgabe> ausgaben;

  public Integer getId() {
    return id;
  }

  public String getName() {
    return gruppeName;
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
  public Gruppe(
      Integer id,
      String gruppeName,
      Boolean offen,
      Set<Mitglied> mitglieder,
      Set<Ausgabe> ausgaben
  ) {
    this.id = id;
    this.gruppeName = gruppeName;
    this.offen = offen;
    this.mitglieder = new HashSet<>(mitglieder);
    this.ausgaben = new HashSet<>(ausgaben);
  }

  public Gruppe(String gruppeName) {
    this(
        null,
        gruppeName,
        true,
        Set.of(),
        Set.of()
    );
  }

  @Override
  public String toString() {
    return "Gruppe{"
        + "id=" + id
        + ", gruppeName='" + gruppeName + '\''
        + ", offen=" + offen
        + ", mitglieder=" + mitglieder
        + ", ausgaben=" + ausgaben
        + '}';
  }
}

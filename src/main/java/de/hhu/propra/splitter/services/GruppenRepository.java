package de.hhu.propra.splitter.services;

import de.hhu.propra.splitter.domain.models.Gruppe;
import java.util.Set;

public interface GruppenRepository {

  Set<Gruppe> getGruppen();

  Long addGruppe(Gruppe gruppe);

  void saveGruppe(Gruppe gruppe);


}

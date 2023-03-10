package de.hhu.propra.splitter.persistence;

import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.data.repository.CrudRepository;

public interface SpringDataGruppenRepository extends CrudRepository<Gruppe, Integer> {

  @Nonnull
  List<Gruppe> findAll();
}

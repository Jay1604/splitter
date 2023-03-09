package de.hhu.propra.splitter.persistence;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface SpringDataGruppenRepository extends CrudRepository<Gruppe, Integer> {

  List<Gruppe> findAll();
}

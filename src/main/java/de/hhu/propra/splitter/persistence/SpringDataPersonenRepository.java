package de.hhu.propra.splitter.persistence;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.springframework.data.repository.CrudRepository;

public interface SpringDataPersonenRepository extends CrudRepository<Person, Integer> {
  @Nonnull
  List<Person> findAll();

  Optional<Person> findBygitHubName(String name);
}

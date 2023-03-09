package de.hhu.propra.splitter.persistence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import org.springframework.data.annotation.Id;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record Gruppe(@Id Integer id, String name, Boolean offen, Set<Person> mitglieder,
                     Set<Ausgabe> ausgaben) {

}

package de.hhu.propra.splitter.persistence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import org.springframework.data.annotation.Id;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record Ausgabe(
    @Id Integer id,
    String beschreibung,
    Long betrag,
    Integer glaeubiger,

    Set<Schuldner> schuldner
) {

}

package de.hhu.propra.splitter.web.rest.objects;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record DetailedGruppeEntity(
    String gruppe, String name,
    List<String> personen,
    Boolean geschlossen,
    List<AusgabeEntity> ausgaben
) {

}

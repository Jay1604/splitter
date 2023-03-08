package de.hhu.propra.splitter.web.rest.objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record AusgabeEntity(String grund, String glaeubiger, Integer cent, List<String> schuldner) {

}

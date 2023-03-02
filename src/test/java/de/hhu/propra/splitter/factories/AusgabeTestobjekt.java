package de.hhu.propra.splitter.factories;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import org.javamoney.moneta.Money;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
record AusgabeTestobjekt(String beschreibung, Money betrag, String glauebiger,
                         Set<String> schuldner) {

}

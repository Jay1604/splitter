package de.hhu.propra.splitter.web.forms;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Set;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record AusgabeHinzufuegenForm(
    @NotNull Long gruppeId,
    @NotBlank @Pattern(regexp = "^\\d+.?\\d?\\d?$") String betrag,
    @NotBlank String beschreibung,
    @NotBlank String glaeubiger,
    @NotEmpty Set<String> schuldner
) {

}

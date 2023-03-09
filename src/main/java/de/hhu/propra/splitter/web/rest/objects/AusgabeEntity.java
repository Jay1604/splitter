package de.hhu.propra.splitter.web.rest.objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record AusgabeEntity(@NotBlank String grund,
                            @NotBlank String glaeubiger,
                            @NotNull @Min(0) Integer cent,
                            @NotEmpty List<String> schuldner) {

}

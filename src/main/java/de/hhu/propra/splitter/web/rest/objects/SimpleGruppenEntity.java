package de.hhu.propra.splitter.web.rest.objects;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record SimpleGruppenEntity(String gruppe,
                                  @NotBlank String name,
                                  @NotEmpty List<String> personen) {}

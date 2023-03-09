package de.hhu.propra.splitter.web.rest.objects;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public record SimpleGruppenEntity(String gruppe,
                                  @NotBlank String name,
                                  @NotEmpty List<@Pattern(
                                      regexp = "^[a-zA-Z\\d](?:[a-zA-Z\\d]"
                                          + "|-(?=[a-zA-Z\\d])){0,38}$",
                                      message = "Muss valider GitHub Name sein"
                                  ) String> personen) {

}

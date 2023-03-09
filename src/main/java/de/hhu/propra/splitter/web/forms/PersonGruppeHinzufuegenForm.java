package de.hhu.propra.splitter.web.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public record PersonGruppeHinzufuegenForm(
    @NotBlank
    @Pattern(
        regexp = "^[a-zA-Z\\d](?:[a-zA-Z\\d]|-(?=[a-zA-Z\\d])){0,38}$",
        message = "Muss valider GitHub Name sein"
    )
    String name,
    @NotNull Long id) {

}

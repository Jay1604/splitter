package de.hhu.propra.splitter.web.forms;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public record GruppeErstellenForm(
    @NotBlank (message = "Gruppenname fehlt") String name

) {

}

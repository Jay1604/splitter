package de.hhu.propra.splitter.web.forms;

import javax.validation.constraints.NotBlank;

public record GruppeErstellenForm(
    @NotBlank(message = "Gruppenname fehlt") String name

) {

}

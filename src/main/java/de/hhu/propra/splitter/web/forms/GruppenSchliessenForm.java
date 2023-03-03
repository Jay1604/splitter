package de.hhu.propra.splitter.web.forms;

import javax.validation.constraints.NotNull;

public record GruppenSchliessenForm(
    @NotNull
    long id
) {
}

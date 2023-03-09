package de.hhu.propra.splitter.web.rest.objects;

public record UeberweisungEntity(
    String von,
    String an,
    Integer cents
) {

}

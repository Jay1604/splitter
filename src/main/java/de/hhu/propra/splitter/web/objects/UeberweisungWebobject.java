package de.hhu.propra.splitter.web.objects;

public record UeberweisungWebobject(
    String empfaenger,
    String betrag,
    String sender,
    String gruppe
) {

}

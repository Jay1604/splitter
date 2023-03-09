package de.hhu.propra.splitter.persistence;

import org.springframework.data.relational.core.mapping.Table;

@Table("ausgabe_person")
public record Schuldner(Integer person) {

}

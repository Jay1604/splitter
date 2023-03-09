package de.hhu.propra.splitter.persistence;

import org.springframework.data.annotation.Id;

public record Person(@Id Integer id, String gitHubName) {

}

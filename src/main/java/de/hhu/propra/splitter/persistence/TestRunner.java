package de.hhu.propra.splitter.persistence;

import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestRunner {

//  @Bean
//  CommandLineRunner test(SpringDataGruppenRepository gruppenRepository) {
//    return args -> {
//      gruppenRepository.deleteAll();
//      Gruppe test = gruppenRepository.save(new Gruppe(null, "test", true, Set.of(new Mitglied(1,)), Set.of()));
//      gruppenRepository.save(test);
//      System.out.println(gruppenRepository.findAll());
//    };
//  }
}

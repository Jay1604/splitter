package de.hhu.propra.splitter.persistence;

import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestRunner {
//  @Bean
//  CommandLineRunner test(SpringDataGruppenRepository gruppenRepository, SpringDataPersonenRepository personenRepository, GruppenRepositoryImpl gruppenRepositoryimp) {
//    return args -> {
//      gruppenRepository.deleteAll();
//      personenRepository.deleteAll();
//      Person person = personenRepository.save(new Person(null, "nutzer1"));
//      Gruppe test = gruppenRepository.save(new Gruppe("test"));
//      test.addMitglied(person);
//      gruppenRepository.save(test);
//      System.out.println(gruppenRepository.findAll());
//
//      de.hhu.propra.splitter.domain.models.Gruppe gruppe = gruppenRepositoryimp.toGruppe(test);
//
//      System.out.println(gruppe);
//      gruppe.addMitglied("person");
//      test = gruppenRepositoryimp.fromGruppe(gruppe);
//      System.out.println(test);
//
//    };
//}
}

package de.hhu.propra.splitter.services;

import de.hhu.propra.splitter.domain.model.Gruppe;
import de.hhu.propra.splitter.domain.model.Person;
import de.hhu.propra.splitter.exception.GruppeNotFound;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GruppenService {

  private Set<Gruppe> gruppen = new HashSet<>();

  private Set<Gruppe> getGruppen() {
    return Set.copyOf(gruppen);
  }

  public Long addGruppe(String gruender, String name) {
    Gruppe gruppe = new Gruppe((long) gruppen.size(), gruender, name);
    gruppen.add(gruppe);
    return gruppe.getId();
  }

  public Set<Gruppe> getGruppenForGithubName(String githubName) {
    return this.getGruppen().stream().filter(
        gruppe -> gruppe.getMitglieder().stream().map(Person::getGitHubName).toList()
            .contains(githubName)
    ).collect(Collectors.toSet());
  }
  public void addUser(String githubName, long gruppenId){

    Gruppe gruppe = gruppen.stream().filter(a -> a.getId().equals(gruppenId)).findFirst()
        .orElseThrow(
            GruppeNotFound::new);

    gruppe.addMitglied(githubName);
  }
}

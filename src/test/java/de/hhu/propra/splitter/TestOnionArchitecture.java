package de.hhu.propra.splitter;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.constructors;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import de.hhu.propra.splitter.stereotypes.AggregateRoot;
import de.hhu.propra.splitter.stereotypes.Entity;

@AnalyzeClasses(
    packagesOf = SplitterApplication.class,
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class TestOnionArchitecture {

  @ArchTest
  static final ArchRule isOnionArchitecture = onionArchitecture()
      .domainModels("..domain.models..")
      .domainServices("..domain.services..")
      .applicationServices("..services..")
      .adapter(
          "database",
          "..persistence.."
      )
      .adapter(
          "web",
          "..web.."
      );

  @ArchTest
  static final ArchRule aggregateRootIsPublic = constructors().that()
      .areDeclaredInClassesThat()
      .resideInAPackage("..domain.models..")
      .and()
      .areDeclaredInClassesThat()
      .areAnnotatedWith(AggregateRoot.class)
      .should()
      .bePublic();

  @ArchTest
  static final ArchRule domainEntitiesArePackagePrivate = constructors().that()
      .areDeclaredInClassesThat()
      .resideInAPackage("..domain.models..")
      .and()
      .areDeclaredInClassesThat()
      .areAnnotatedWith(Entity.class)
      .should()
      .bePackagePrivate();

}



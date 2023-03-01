package de.hhu.propra.splitter;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packagesOf = SplitterApplication.class,
    importOptions = ImportOption.DoNotIncludeTests.class
)
public class TestOnionArchitecture {
  @ArchTest
  static final ArchRule isOnionArchitecture = onionArchitecture()
      .domainModels("..domain.model..")
      .domainServices("..domain.service..")
      .applicationServices("..services..")
      .adapter("database", "..persistence..")
      .adapter("web", "..web..");
}

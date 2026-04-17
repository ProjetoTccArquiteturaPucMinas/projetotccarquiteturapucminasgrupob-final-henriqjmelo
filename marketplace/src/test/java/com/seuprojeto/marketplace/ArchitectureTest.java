package com.seuprojeto.marketplace;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

class ArchitectureTest {

    @Test
    void domainShouldNotDependOnOtherLayers() {

        var classes = new ClassFileImporter()
                .withImportOption((ImportOption) location -> !location.contains("jrt:/"))
                .importPackages("com.seuprojeto.marketplace");

        ArchRuleDefinition.noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..application..", "..infrastructure..", "..presentation..")
                .check(classes);
    }
}
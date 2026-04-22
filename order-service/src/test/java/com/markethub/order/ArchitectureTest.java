package com.markethub.order;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

class ArchitectureTest {

    private final com.tngtech.archunit.core.domain.JavaClasses classes =
            new ClassFileImporter().importPackages("com.markethub.order");

    @Test
    void domainShouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..");
        rule.check(classes);
    }

    @Test
    void domainShouldNotDependOnSpringFramework() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAPackage("org.springframework..");
        rule.check(classes);
    }

    @Test
    void controllersShouldOnlyBeInApiPackage() {
        ArchRule rule = noClasses()
                .that().haveSimpleNameEndingWith("Controller")
                .should().resideOutsideOfPackage("..api.controller..");
        rule.check(classes);
    }

    @Test
    void repositoriesShouldOnlyBeInInfrastructureOrDomain() {
        ArchRule rule = noClasses()
                .that().haveSimpleNameEndingWith("Repository")
                .should().resideOutsideOfPackages("..domain.repository..", "..infrastructure.persistence..");
        rule.check(classes);
    }

    @Test
    void layeredArchitectureShouldBeRespected() {
        layeredArchitecture()
                .consideringAllDependencies()
                .layer("API").definedBy("..api..")
                .layer("Application").definedBy("..application..")
                .layer("Domain").definedBy("..domain..")
                .layer("Infrastructure").definedBy("..infrastructure..")
                .whereLayer("API").mayOnlyBeAccessedByLayers("Application")
                .whereLayer("Domain").mayNotAccessAnyLayer()
                .check(classes);
    }
}

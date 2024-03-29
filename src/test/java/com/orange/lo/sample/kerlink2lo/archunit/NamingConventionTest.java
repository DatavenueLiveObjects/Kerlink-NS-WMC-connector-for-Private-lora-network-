package com.orange.lo.sample.kerlink2lo.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(
        packages = "com.orange.lo.sample.kerlink2lo",
        importOptions = {
                ImportOption.DoNotIncludeTests.class,
                ImportOption.DoNotIncludeJars.class
        }
)
public class NamingConventionTest {

    @ArchTest
    void configurationAnnotatedClassesShouldEndWithConfig(JavaClasses classes) {
        classes().that().areAnnotatedWith(Configuration.class)
                .should().haveSimpleNameEndingWith("Config")
                .check(classes);
    }

    @ArchTest
    void springBootApplicationAnnotatedClassesEndWithApplication(JavaClasses classes) {
        classes().that().areAnnotatedWith(SpringBootApplication.class)
                .should().haveSimpleNameEndingWith("Application")
                .check(classes);
    }

    @ArchTest
    void springBootApplicationAnnotatedClassesEndWithApplication2(JavaClasses classes) {
        classes().that().areAssignableTo(Exception.class)
                .should().haveSimpleNameEndingWith("Exception")
                .check(classes);
    }
}
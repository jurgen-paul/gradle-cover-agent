package ai.qodo.cover.plugin

import org.gradle.api.logging.Logger
import spock.lang.Specification
import spock.lang.TempDir

class TestFileGeneratorImplSpec extends Specification {
    @TempDir
    File testProjectDir

    // Successfully generates test file from Java source file with valid package name
    def "should generate test file when given Java source with valid package"() {
        given:
        def logger = Mock(Logger)
        def sourceFile = new File(testProjectDir, clazz + ".java")

        Map<String, File> testSourceDirectory = Map.of(framework, testProjectDir)
        def generator = new TestFileGeneratorImpl(testSourceDirectory, logger)
        String value = pack + ";\nclass " + clazz + " {}"
        sourceFile.write(value)

        when:
        def result = generator.generate(sourceFile, framework)

        then:
        result.isPresent()
        result.get().name == genTest + extention
        result.get().text.contains(pack)
        result.get().text.contains("class " + genTest)
        result.get().isFile()
        result.get().exists()

        where:
        pack                  | framework        | clazz       | genTest         | extention
        "package com.example" | "spockframework" | "TheBigLab" | "TheBigLabSpec" | ".groovy"
        "package ai.qodo"     | "junit5"         | "BigJLab"   | "BigJLabTest"   | ".java"
        "package org.another" | "junit4"         | "BigJ4Lab"  | "BigJ4LabTest"  | ".java"
        "package org.another" | "junit3"         | "BigJ3Lab"  | "BigJ3LabTest"  | ".java"
        "package org.tst.ng"  | "testng"         | "BigTNLab"  | "BigTNLabTest"  | ".java"

    }

}

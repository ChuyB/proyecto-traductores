plugins {
  application
  antlr
}

repositories {
  mavenCentral()
}

dependencies {
  antlr("org.antlr:antlr4:4.13.1")
  testImplementation(libs.junit)
  implementation(libs.guava)
  implementation("org.antlr:antlr4-runtime:4.13.1")
}

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

application {
  mainClass = "org.example.App"
}

tasks {
  generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-visitor", "-package", "org.example")
    outputDirectory = file("${project.layout.buildDirectory.get()}/generated-src/antlr/main/org/example/")
  }

  jar {
    dependsOn("generateGrammarSource")
    manifest.attributes["Main-Class"] = "org.example.App"
    val dependencies = configurations
      .runtimeClasspath
      .get()
      .map { zipTree(it) }

    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
  }
}

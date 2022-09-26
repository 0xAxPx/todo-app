import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

//Global variables
val allureVersion = "2.9.0"
val junitVersion = "5.3.1"
val sl4jVersion = "2.0.0-alpha7"

plugins {
    kotlin("jvm") version "1.6.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.2")
    implementation("org.slf4j:slf4j-api:$sl4jVersion")
    testImplementation("org.slf4j:slf4j-simple:$sl4jVersion")

    implementation("org.apache.commons:commons-math3:3.6.1")

    // JUnit5
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testImplementation("org.assertj:assertj-core:3.11.1")

}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("PASSED", "FAILED", "SKIPPED", "STANDARD_OUT", "STANDARD_ERROR")
    }
    testLogging.showStandardStreams = true
    filter {
        includeTestsMatching("com.todo.*")
    }

}

configurations {
    create("testCompile")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    // id("org.jetbrains.kotlin.jvm") version "1.3.72"
    kotlin("jvm")
    id("io.kotest") version "0.2.6"

    // Apply the application plugin to add support for building a CLI application.
    application
}

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val ktlint by configurations.creating

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.ionspin.kotlin:bignum:0.2.3")

    ktlint("com.pinterest:ktlint:0.40.0")

    implementation(project(":core"))
}

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.jlleitschuh.gradle:ktlint-gradle:9.4.1")
    }
}
apply(plugin = "org.jlleitschuh.gradle.ktlint")

application {
    // Define the main class for the application.
    mainClassName = "com.github.shwaka.kohomology.AppKt"
}

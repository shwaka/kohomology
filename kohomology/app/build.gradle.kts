plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    // id("org.jetbrains.kotlin.jvm") version "1.3.72"
    kotlin("jvm")
    id("io.kotest") version "0.2.6"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"

    // Apply the application plugin to add support for building a CLI application.
    application
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.ionspin.kotlin:bignum:0.2.3")

    implementation(project(":core"))
}

application {
    // Define the main class for the application.
    mainClassName = "com.github.shwaka.kohomology.AppKt"
}

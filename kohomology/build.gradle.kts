plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    // id("org.jetbrains.kotlin.jvm") version "1.3.72"
    kotlin("multiplatform") version "1.4.10"
    id("io.kotest") version "0.2.6"
}

group = "com.github.shwaka"
version = "0.1"

repositories {
    // Use jcenter for resolving dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        /*testRuns["test"].executionTask.configure {
            useJunit()
        }*/
    }
    js(IR) {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Align versions of all Kotlin components
                implementation("org.jetbrains.kotlin:kotlin-bom")
                // Use the Kotlin JDK 8 standard library.
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
                implementation("com.ionspin.kotlin:bignum:0.2.3")
            }
        }
        val jvmTest by getting {
            dependencies {
                // kotest
                val version = "4.3.2"
                implementation("io.kotest:kotest-runner-junit5:$version")
                implementation("io.kotest:kotest-assertions-core:$version")
                implementation("io.kotest:kotest-property:$version")
                implementation("io.kotest:kotest-assertions-compiler:$version")
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<Test>("jvmTest") {
    // https://github.com/kotest/kotest/issues/1105
    useJUnitPlatform()
    testLogging {
        showExceptions = true
        showStandardStreams = true
        events = setOf(org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED, org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED)
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

val ktlint by configurations.creating

// dependencies {
//    // Align versions of all Kotlin components
//    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

//    // Use the Kotlin JDK 8 standard library.
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

//    implementation("com.ionspin.kotlin:bignum:0.2.3")

//    // Use the Kotlin test library.
//    testImplementation("org.jetbrains.kotlin:kotlin-test")

//    // Use the Kotlin JUnit integration.
//    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

//    // kotest
//    val version = "4.3.2"
//    testImplementation("io.kotest:kotest-runner-junit5:$version")
//    testImplementation("io.kotest:kotest-assertions-core:$version")
//    testImplementation("io.kotest:kotest-property:$version")
//    testImplementation("io.kotest:kotest-assertions-compiler:$version")

//    ktlint("com.pinterest:ktlint:0.40.0")
// }

buildscript {
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.jlleitschuh.gradle:ktlint-gradle:9.4.1")
    }
}
apply(plugin = "org.jlleitschuh.gradle.ktlint")

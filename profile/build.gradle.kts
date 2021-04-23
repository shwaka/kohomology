import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    application
}

group = "me.shun"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("../repository")
    }
    maven(url = "https://shwaka.github.io/maven/")
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("org.slf4j:slf4j-nop:1.7.30")
    implementation("com.github.shwaka.kohomology:kohomology:0.5-SNAPSHOT")
    implementation("com.github.shwaka.counter:simple-counter:0.2")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = "MainKt"
}

tasks.withType<JavaExec> {
    standardInput = System.`in`
}

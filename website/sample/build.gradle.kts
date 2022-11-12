import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    application
}

group = "me.shun"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://shwaka.github.io/maven/")
}

dependencies {
    testImplementation(kotlin("test-junit"))
    // Use released version intentionally (see .github/workflows/run_sample.yml)
    implementation("com.github.shwaka.kohomology:kohomology:0.10")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

// aliases
tasks.register("kc") { dependsOn("ktlintCheck") }
tasks.register("kf") { dependsOn("ktlintFormat") }

fun convertSampleName(sampleName: String): String {
    // foo.kt -> FooKt
    // foo -> Foo
    // Foo -> Foo
    return sampleName.capitalize().removeSuffix(".kt") + "Kt"
}

application {
    val sampleName: String = System.getProperty("sampleName") ?: "SampleNameNotSet"
    mainClassName = "com.github.shwaka.kohomology.sample.${convertSampleName(sampleName)}"
}

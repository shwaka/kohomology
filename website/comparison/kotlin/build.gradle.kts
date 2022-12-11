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

fun convertName(name: String): String {
    // foo.kt -> FooKt
    // foo -> FooKt
    // Foo -> FooKt
    return name.capitalize().removeSuffix(".kt") + "Kt"
}

application {
    val name: String = System.getProperty("name") ?: "NameNotSet"
    mainClassName = "com.github.shwaka.kohomology.sample.${convertName(name)}"

    val degree: String = System.getProperty("degree") ?: "10"
    applicationDefaultJvmArgs = listOf(
        "-Ddegree=$degree"
    )
}

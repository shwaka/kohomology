import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    application
    kotlin("plugin.allopen") version "1.4.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.3.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.4.30"
}

group = "me.shun"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    // maven {
    //     url = uri("../repository")
    // }
    mavenLocal()
    maven(url = "https://shwaka.github.io/maven/")
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime-jvm:0.3.0")
    implementation("com.github.shwaka.kohomology:kohomology:0.6-SNAPSHOT")
    implementation("com.github.shwaka.counter:simple-counter:0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Wrapper> {
    gradleVersion = "6.6.1"
}

application {
    mainClassName = "com.github.shwaka.kohomology.profile.KohomologyProfileKt"
}

tasks.withType<JavaExec> {
    // required for readLine() (in profile)
    standardInput = System.`in`
}

configure<org.jetbrains.kotlin.allopen.gradle.AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    configurations {
        named("main") {
            iterations = 3
            warmups = 0
            mode = "AverageTime"
        }
    }
    targets {
        register("main")
    }
}

// aliases
tasks.register("kc") { dependsOn("ktlintCheck") }
tasks.register("kf") { dependsOn("ktlintFormat") }

task("formatBenchmarkResult", JavaExec::class) {
    main = "com.github.shwaka.kohomology.profile.FormatBenchmarkResultKt"
    classpath = sourceSets["main"].runtimeClasspath
}

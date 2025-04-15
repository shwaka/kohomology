import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    application
    kotlin("plugin.allopen") version "1.4.0"
    // Note: kotlinx.benchmark of version < 0.4.0 does not support gradle 7
    // https://github.com/Kotlin/kotlinx-benchmark/issues/68
    id("org.jetbrains.kotlinx.benchmark") version "0.4.13"
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
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime-jvm:0.4.0")
    implementation("com.github.shwaka.kohomology:kohomology:0.14-SNAPSHOT")
    implementation("com.github.shwaka.counter:simple-counter:0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.github.shwaka.kohomology.profile.KohomologyProfileKt")
}

tasks.withType<JavaExec> {
    // required for readLine() (in profile)
    standardInput = System.`in`
}

configure<org.jetbrains.kotlin.allopen.gradle.AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    val benchmarkTarget: String? = System.getProperty("benchmarkTarget")
    configurations {
        named("main") {
            iterations = 3
            warmups = 0
            mode = "AverageTime"
            if (benchmarkTarget != null) {
                include(benchmarkTarget) // a substring of fully qualified names
            }
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
    mainClass.set("com.github.shwaka.kohomology.profile.FormatBenchmarkResultKt")
    classpath = sourceSets["main"].runtimeClasspath
}

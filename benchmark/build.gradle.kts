import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
// import kotlinx.benchmark.gradle.*
// import org.jetbrains.kotlin.allopen.gradle.*

plugins {
    kotlin("jvm") version "1.4.32"
    kotlin("plugin.allopen") version "1.4.0"
    id("org.jetbrains.kotlinx.benchmark") version "0.3.0"
}

configure<org.jetbrains.kotlin.allopen.gradle.AllOpenExtension> {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    configurations {
        named("main") {
            iterations = 1
            warmups = 0
            mode = "AverageTime"
        }
    }
    targets {
        register("main")
    }
}

group = "me.shun"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("repository")
    }
    maven(url = "https://shwaka.github.io/maven/")
}

dependencies {
    testImplementation(kotlin("test-junit"))
    implementation("org.slf4j:slf4j-nop:1.7.30")
    implementation("org.jetbrains.kotlinx:kotlinx-benchmark-runtime-jvm:0.3.0")
    implementation("com.github.shwaka.kohomology:kohomology:0.2-SNAPSHOT")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

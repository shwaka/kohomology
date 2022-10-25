plugins {
    val kotlinVersion = "1.5.32"
    kotlin("js") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

group = "me.shun"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal() // install kohomology from mavenLocal
    jcenter()
    mavenCentral()
    maven { url = uri("https://shwaka.github.io/maven/") }
}

dependencies {
    testImplementation(kotlin("test-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
    implementation("com.github.shwaka.kohomology:kohomology:0.10-SNAPSHOT")
}

kotlin {
    js(IR) {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
}

// aliases
tasks.register("kc") { dependsOn("ktlintCheck") }
tasks.register("kf") { dependsOn("ktlintFormat") }

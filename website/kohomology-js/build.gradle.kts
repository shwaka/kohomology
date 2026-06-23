plugins {
    val kotlinVersion = "1.9.10"
    kotlin("multiplatform") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

group = "me.shun"
version = "1.0-SNAPSHOT"

// bignum 0.3.10 pulls Kotlin/JS 2.x runtime artifacts, but this project is compiled with Kotlin 1.9.x.
// Keep bignum on a Kotlin 1.x-compatible release to avoid missing Kotlin runtime symbols in webapp tests.
val bignumVersion = "0.3.0"

configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.ionspin.kotlin" && requested.name.startsWith("bignum")) {
            useVersion(bignumVersion)
        }
    }
}

repositories {
    mavenLocal() // install kohomology from mavenLocal
    mavenCentral()
    maven { url = uri("https://shwaka.github.io/maven/") }
}

kotlin {
    js(IR) {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport {
                    enabled.set(true)
                }
            }
            runTask {
                cssSupport {
                    enabled.set(true)
                }
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport {
                        enabled.set(true)
                    }
                }
            }
        }

        // generate .d.ts (type definition file for TypeScript)
        generateTypeScriptDefinitions()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                // testImplementation(kotlin("test-js"))
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                implementation("com.github.shwaka.kohomology:kohomology:0.14-SNAPSHOT")
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

// aliases
tasks.register("lc") { dependsOn("ktlintCheck") }
tasks.register("lf") { dependsOn("ktlintFormat") }

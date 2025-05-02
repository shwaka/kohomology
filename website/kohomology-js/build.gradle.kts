plugins {
    val kotlinVersion = "1.9.10"
    kotlin("multiplatform") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
}

group = "me.shun"
version = "1.0-SNAPSHOT"

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
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0")
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

plugins {
    kotlin("js") version "1.4.10"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

group = "me.shun"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("com.ionspin.kotlin:bignum:0.2.3")
    testImplementation(kotlin("test-js"))
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

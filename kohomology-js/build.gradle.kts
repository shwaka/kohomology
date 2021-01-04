plugins {
    kotlin("js") version "1.4.10"
}

group = "me.shun"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    flatDir {
        dirs("../kohomology/core/build/libs")
    }
}

dependencies {
    testImplementation(kotlin("test-js"))
    implementation("kohomology:kohomology-js")
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
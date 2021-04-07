plugins {
    kotlin("js") version "1.4.31"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

group = "me.shun"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    // mavenLocal()
    maven(url = "https://shwaka.github.io/maven/")
}

dependencies {
    implementation("com.ionspin.kotlin:bignum:0.2.3")
    implementation("com.github.shwaka.kohomology:kohomology:0.0")
    testImplementation(kotlin("test-js"))
    // kococo
    val kococoVersion = "0.1"
    val kococoDebug = "com.github.shwaka.kococo:kococo-debug-js:$kococoVersion"
    val kococoRelease = "com.github.shwaka.kococo:kococo-release-js:$kococoVersion"
    if (System.getProperty("kococo.debug") == null) {
        implementation(kococoRelease)
    } else {
        implementation(kococoDebug)
    }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.3")
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

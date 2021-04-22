import org.apache.tools.ant.taskdefs.condition.Os

group = "com.github.shwaka.kohomology"
version = "0.4-SNAPSHOT"

plugins {
    kotlin("multiplatform") version "1.4.31"
    id("io.kotest") version "0.2.6"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    id("java-library") // necessary for jacoco
    jacoco
    id("com.adarshr.test-logger") version "3.0.1-SNAPSHOT"
    `maven-publish`
}

apply<com.github.shwaka.kohomology.MyPlugin>()

repositories {
    jcenter()
    maven(url = "https://shwaka.github.io/maven/")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(BOTH) {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                // Use the Kotlin JDK 8 standard library.
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

                implementation("com.ionspin.kotlin:bignum:0.2.8")

                // kococo
                val kococoVersion = "0.1"
                val kococoDebug = "com.github.shwaka.kococo:kococo-debug:$kococoVersion"
                val kococoRelease = "com.github.shwaka.kococo:kococo-release:$kococoVersion"
                if (System.getProperty("kococo.debug") == null) {
                    implementation(kococoRelease)
                } else {
                    implementation(kococoDebug)
                }

                implementation("com.github.shwaka.counter:simple-counter:0.2")
                implementation("io.github.microutils:kotlin-logging:2.0.6")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")
                implementation("com.github.h0tk3y.betterParse:better-parse:0.4.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                // Use the Kotlin test library.
                // implementation("org.jetbrains.kotlin:kotlin-test")

                // Use the Kotlin JUnit integration.
                // implementation("org.jetbrains.kotlin:kotlin-test-junit")
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                // kotest
                val version = "4.3.2"
                implementation("io.kotest:kotest-runner-junit5:$version")
                implementation("io.kotest:kotest-assertions-core:$version")
                implementation("io.kotest:kotest-property:$version")
                implementation("io.kotest:kotest-assertions-compiler:$version")
                // logger
                // implementation("org.slf4j:slf4j-simple:1.7.26")
                implementation("ch.qos.logback:logback-classic:1.2.3")
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeMain by getting
        val nativeTest by getting
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("skipped", "passed", "failed") // "started" は消した
    }
    System.getProperty("kotest.tags")?.let {
        // null を set するとなんかエラーが起きるので、 ?.let を使った
        systemProperties["kotest.tags"] = it
    }
    System.getProperty("kococo.debug")?.let {
        systemProperties["kococo.debug"] = it
    }
    // ↓公式ドキュメント では下のコードが書かれてるけどダメだった
    //   (https://kotest.io/docs/framework/tags.html)
    // systemProperties = System.getProperties().map { it.key.toString() to it.value }.toMap()
    System.getProperty("logLevel")?.let {
        // see resources/logback.xml
        systemProperties["com.github.shwaka.kohomology.logLevel"] = it
    }
}

tasks.jacocoTestReport {
    reports {
        html.isEnabled = true
        xml.isEnabled = true // for codecov.io
    }

    // https://stackoverflow.com/questions/59802396/kotlin-multiplatform-coverage
    val coverageSourceDirs = arrayOf("src/commonMain/kotlin", "src/jvmMain/kotlin")
    val classFiles = File("$buildDir/classes/kotlin/jvm/main").walkBottomUp().toSet()

    classDirectories.setFrom(classFiles)
    sourceDirectories.setFrom(files(coverageSourceDirs))

    executionData.setFrom(files("$buildDir/jacoco/jvmTest.exec"))
}

testlogger {
    theme = com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA
    showCauses = false
    showStandardStreams = true
    showFullStackTraces = true
    filterFullStackTraces = "io\\.kotest.*"
}

fun inWSL(): Boolean {
    // in WSL:
    //   os.name:    Linux
    //   os.version: 4.4.0-18362-Microsoft
    //   os.arch:    amd64
    return System.getProperty("os.version").contains("Microsoft")
}

// val browserCommand = "google-chrome"
val browserCommand = when {
    inWSL() -> "wsl-open" // Os.isFamily だと WSL も UNIX 扱いになる
    Os.isFamily(Os.FAMILY_UNIX) -> "xdg-open"
    else -> throw NotImplementedError("browserCommand is not set for the current OS")
}
tasks.register<Exec>("openTestReport") {
    commandLine(browserCommand, "./build/reports/tests/jvmTest/index.html")
}
tasks.register<Exec>("openJacocoReport") {
    commandLine(browserCommand, "./build/reports/jacoco/test/html/index.html")
}

tasks.withType<Wrapper> {
    gradleVersion = "6.6.1"
}

publishing {
    repositories {
        maven {
            url = uri("../../maven/repository")
            name = "MyMaven"
        }
        maven {
            url = uri("../repository")
            name = "Temporary"
        }
    }
}

tasks.register("kc") {
    // alias
    dependsOn("ktlintCheck")
}

tasks.register("kf") {
    // alias
    dependsOn("ktlintFormat")
}

import com.github.shwaka.kohomology.MyTestListener
import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin.
    // id("org.jetbrains.kotlin.jvm") version "1.3.72"
    kotlin("jvm")
    id("io.kotest") version "0.2.6"
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
    jacoco
    id("com.adarshr.test-logger") version "2.1.1"
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

    // https://github.com/radarsh/gradle-test-logger-plugin/issues/145
    addTestListener(MyTestListener)
}

// 上の addTestListener を参照
gradle.buildFinished {
    MyTestListener.printSummary(logger)
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.ionspin.kotlin:bignum:0.2.3")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    // kotest
    val version = "4.3.2"
    testImplementation("io.kotest:kotest-runner-junit5:$version")
    testImplementation("io.kotest:kotest-assertions-core:$version")
    testImplementation("io.kotest:kotest-property:$version")
    testImplementation("io.kotest:kotest-assertions-compiler:$version")

    // kococo
    val kococoVersion = "0.1"
    val kococoDebug = "com.github.shwaka.kococo:kococo-debug-jvm:$kococoVersion"
    val kococoRelease = "com.github.shwaka.kococo:kococo-release-jvm:$kococoVersion"
    if (System.getProperty("kococo.debug") == null) {
        implementation(kococoRelease)
    } else {
        implementation(kococoDebug)
    }
}

tasks.jacocoTestReport {
    reports {
        html.isEnabled = true
        // xml.isEnabled = true
    }
}

testlogger {
    theme = com.adarshr.gradle.testlogger.theme.ThemeType.MOCHA
    showCauses = true
    showStandardStreams = true
    showFullStackTraces = true
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
    commandLine(browserCommand, "./build/reports/tests/test/index.html")
}
tasks.register<Exec>("openJacocoReport") {
    commandLine(browserCommand, "./build/reports/jacoco/test/html/index.html")
}

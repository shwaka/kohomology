plugins {
    kotlin("jvm") version "1.4.10"
}

repositories {
    jcenter()
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
    repositories {
        jcenter()
        mavenLocal()
        maven { setUrl("https://dl.bintray.com/shwaka/maven/") }
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "6.6.1"
}

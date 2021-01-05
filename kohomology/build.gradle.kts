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
}

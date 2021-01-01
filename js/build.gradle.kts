plugins {
    kotlin("js") version "1.4.21"
}

kotlin {
    js {
        browser {}
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-js"))
}

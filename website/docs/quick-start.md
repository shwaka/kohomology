---
title: Quick start
sidebar_position: 2
---

`kohomology` is a [Kotlin](https://kotlinlang.org/) library published at the maven repository [shwaka/maven](https://github.com/shwaka/maven).
You can use it in any kotlin project.

## Requirement
You need to install Java Development Kit (JDK).
Here we give an example of installation, but most JDK distributions and versions should be OK.

- [Windows] Install from [Amazon Corretto](https://docs.aws.amazon.com/corretto/latest/corretto-8-ug/downloads-list.html). Usually you will choose [amazon-corretto-8-x64-windows-jdk.msi](https://corretto.aws/downloads/latest/amazon-corretto-8-x64-windows-jdk.msi)
- [Mac, Linux] First, install [sdkman](https://sdkman.io/). Then run `sdk install java 8.292.10.1-amzn`.

## Quick start
The repository [shwaka/kohomology-app](https://github.com/shwaka/kohomology-app) provides a working example.
If you are not familiar with kotlin projects, this is a good starting point.

## In your gradle project
If you have not yet, please create a gradle project as in [Get started with Kotlin/JVM | Kotlin](https://kotlinlang.org/docs/jvm-get-started.html).
Then write the following in your `build.gradle.kts`:

```kotlin
repositories {
    maven(url = "https://shwaka.github.io/maven/")
}

dependencies {
    implementation("com.github.shwaka.kohomology:kohomology:0.5")
}
```

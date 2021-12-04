---
sidebar_label: Quick start
sidebar_position: 2
---

Since `kohomology` is a [Kotlin](https://kotlinlang.org/) library, you can use it in any kotlin project.
The repository [shwaka/kohomology-app](https://github.com/shwaka/kohomology-app) provides a working example.

This library is published at the maven repository [shwaka/maven](https://github.com/shwaka/maven).
With gradle, you can use it by:
```kotlin
repositories {
    maven(url = "https://shwaka.github.io/maven/")
}

dependencies {
    implementation("com.github.shwaka.kohomology:kohomology:0.5")
}
```

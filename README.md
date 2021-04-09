## Overview
![classes](uml/packages.png)

## Usage
See
- tests in [kohomology/src/jvmTest/kotlin/com/github/shwaka/kohomology](kohomology/src/jvmTest/kotlin/com/github/shwaka/kohomology)
- sample applications in [shwaka/kohomology-app](https://github.com/shwaka/kohomology-app)

## Test
以下のいずれか．
test の表示には [radarsh/gradle-test-logger-plugin](https://github.com/radarsh/gradle-test-logger-plugin) を利用している．

- `./gradlew jvmTest -Dkotest.tags='Field & !Compile'`
- `./gradlew jvmTest --tests com.github.shwaka.kohomology.IntRationalDenseNumVectorTest`
- `./gradlew jvmTest --tests "*DenseNumVectorTest"`

注意: `!` を含む場合は zsh に解釈されるのを防ぐために，double quote ではなく single quote を使う必要がある．

- test report は `./gradlew openTestReport` で表示できる．
- log level は `-DlogLevel=DEBUG` などで指定できる．

## Coverage
```bash
cd kohomology
./gradlew jvmTest  # 先に test を実行する必要がある
./gradlew jacocoTestReport
./gradlew openJacocoReport
# ↑open build/reports/jacoco/test/html/index.html と同じ
```

## Publish
Put the `repository` directory at the following place.
You can change the place by editing `build.gradle.kts`.

```
./
├── maven/
│  └── repository/
|     └── com/github/shwaka/kohomology/
└── kohomology/
   └── kohomology/
      ├── gradlew
      ├── build.gradle.kts
      └── src/
```

```bash
cd kohomology/kohomology
./gradlew publishAllPublicationsToMyMavenRepository
```

## Benchmark
```bash
cd kohomology/kohomology
./gradlew publishAllPublicationsToBenchmarkRepository
cd ../benchmark
./gradlew benchmark
```

## Recursive generics
当初は `interface Scalar<S : Scalar<S>>` みたいに再帰的な定義をしてた．
ちゃんと安定して動作するのか不安だったけど，例えば (`interface` じゃなくて `abstract class` だけど) `Enum` でも使われているっぽいので，多分大丈夫．
[kotlin/Enum.kt at master · JetBrains/kotlin](https://github.com/JetBrains/kotlin/blob/master/core/builtins/native/kotlin/Enum.kt)

参考になりそうなリンクたちを列挙しておく．
(どれも長いのであんまりちゃんと読んでない)
- [Self Types - Language Design - Kotlin Discussions](https://discuss.kotlinlang.org/t/self-types/371/21)
- [Self Types with Java's Generics - SitePoint](https://www.sitepoint.com/self-types-with-javas-generics/)
- [Emulating self types in Kotlin. DIY solution for missing language… | by Jerzy Chałupski | Medium](https://medium.com/@jerzy.chalupski/emulating-self-types-in-kotlin-d64fe8ea2e62)

## Version of java
native 向けにコンパイルしようとしたら，以下のエラーが出た．
依存関係のダウンロードをする際に，証明書関係で失敗しているっぽい．
使用する java のバージョンを変えたらうまくいった．

- `10.0.2-open`: NG
- `10.0.2-zulu`: OK

```
Downloading native dependencies (LLVM, sysroot etc). This is a one-time action performed only on the first run of the compiler.
Cannot download a dependency: javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```

## overview
![classes](uml/packages.png)

## test
以下のいずれか．
test の表示には [radarsh/gradle-test-logger-plugin](https://github.com/radarsh/gradle-test-logger-plugin) を利用している．

- `./gradlew test -Dkotest.tags='Field & !Compile'`
- `./gradlew test --tests com.github.shwaka.kohomology.IntRationalDenseNumVectorTest`
- `./gradlew test --tests "*DenseNumVectorTest"`

注意: `!` を含む場合は zsh に解釈されるのを防ぐために，double quote ではなく single quote を使う必要がある．

test report は `./gradlew openTestReport` で表示できる．

## coverage
```bash
cd kohomology
./gradlew test  # 先に test を実行する必要がある
./gradlew core:jacocoTestReport
./gradlew openJacocoReport
# ↑open core/build/reports/jacoco/test/html/index.html と同じ
```

## multiplatform をやめた理由
- `kotest` など，JVM 用のライブラリが使えない
    - `jvmTest` 内で実行すれば `kotest` そのものは動いた
    - しかし [kotest/kotest-gradle-plugin](https://github.com/kotest/kotest-gradle-plugin) は動かないっぽい．
      そもそもこの plugin が(multiplatform 以前の問題として)あんまりメンテナンスされてなさそう．
- multiplatform はまだ新しい機能なので，代替品が充実してなさそう

## Scalar などの定義について
`interface Scalar<S : Scalar<S>>` みたいに再帰的な定義をしてるのが不安だったけど，例えば (`interface` じゃなくて `abstract class` だけど) `Enum` でも使われているっぽいので，多分大丈夫．
[kotlin/Enum.kt at master · JetBrains/kotlin](https://github.com/JetBrains/kotlin/blob/master/core/builtins/native/kotlin/Enum.kt)

参考になりそうなリンクたちを列挙しておく．
(どれも長いのであんまりちゃんと読んでない)
- [Self Types - Language Design - Kotlin Discussions](https://discuss.kotlinlang.org/t/self-types/371/21)
- [Self Types with Java's Generics - SitePoint](https://www.sitepoint.com/self-types-with-javas-generics/)
- [Emulating self types in Kotlin. DIY solution for missing language… | by Jerzy Chałupski | Medium](https://medium.com/@jerzy.chalupski/emulating-self-types-in-kotlin-d64fe8ea2e62)

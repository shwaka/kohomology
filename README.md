## test
以下のいずれか．
test の表示には [radarsh/gradle-test-logger-plugin](https://github.com/radarsh/gradle-test-logger-plugin) を利用している．

- `./gradlew test -Dkotest.tags='Field & !Compile'`
- `./gradlew test --tests com.github.shwaka.kohomology.IntRationalDenseNumVectorTest`
- `./gradlew test --tests "*DenseNumVectorTest"`

注意: `!` を含む場合は zsh に解釈されるのを防ぐために，double quote ではなく single quote を使う必要がある．

### kotest (古い情報)
- `./gradlew kotest`
- `./gradlew kotest -Dkotest.tags='Field & !Compile'`

[kotest-gradle-plugin](https://github.com/kotest/kotest-gradle-plugin) を使っていた時代のもの．
gradle-test-logger-plugin の方がメンテナンス状況が良さそうなので以降した．

## coverage
```bash
cd kohomology
./gradlew test  # 先に test を実行する必要がある
./gradlew core:jacocoTestReport
open core/build/reports/jacoco/test/html/index.html
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

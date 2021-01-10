## test
以下のいずれか

- `./gradlew kotest`
- `./gradlew kotest -Dkotest.tags="DenseNumVector"`
- `./gradlew test -Dkotest.tags="DenseNumVector"`
- `./gradlew test --tests com.github.shwaka.kohomology.IntRationalDenseNumVectorTest`
- `./gradlew test --tests "*DenseNumVectorTest"`

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

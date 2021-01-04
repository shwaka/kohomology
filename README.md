## multiplatform をやめた理由
- `kotest` など，JVM 用のライブラリが使えない
    - `jvmTest` 内で実行すれば `kotest` そのものは動いた
    - しかし [kotest/kotest-gradle-plugin](https://github.com/kotest/kotest-gradle-plugin) は動かないっぽい．
      そもそもこの plugin が(multiplatform 以前の問題として)あんまりメンテナンスされてなさそう．
- multiplatform はまだ新しい機能なので，代替品が充実してなさそう

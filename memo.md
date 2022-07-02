*CAUTION: This file is not intended to be read by other people and hence written in Japanese (partially).*

## kohomology
### Commitlint
```
cd scripts/commitlint
npm install
npm run prepare
```

### Test
以下のいずれか．
test の表示には [radarsh/gradle-test-logger-plugin](https://github.com/radarsh/gradle-test-logger-plugin) を利用している．

- `./gradlew jvmTest -Dkotest.tags='Field & !Compile'`
- `./gradlew jvmTest --tests com.github.shwaka.kohomology.IntRationalDenseNumVectorTest`
- `./gradlew jvmTest --tests "*DenseNumVectorTest"`

注意: `!` を含む場合は zsh に解釈されるのを防ぐために，double quote ではなく single quote を使う必要がある．

- test report は `./gradlew openTestReport` で表示できる．
- log level は `-DlogLevel=DEBUG` などで指定できる．

### Coverage
```bash
cd kohomology
./gradlew jvmTest  # 先に test を実行する必要がある
./gradlew jacocoTestReport
./gradlew openJacocoReport
# ↑open build/reports/jacoco/test/html/index.html と同じ
```

### Publish
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

- Bump version to `1.0` with a git tag `v1.0` and publish it: `./bump-version.sh release 1.0`
- Bump version to `1.0-SNAPSHOT`: `./bump-versin.sh snapshot 1.0`

### Benchmark
```bash
cd kohomology/kohomology
./gradlew publishAllPublicationsToBenchmarkRepository
cd ../profile
./gradlew benchmark
```

### Profiling
1. `sudo sysctl kernel.perf_event_paranoid=1`
    - or add `kernel.perf_event_paranoid=1` to `/etc/sysctl.conf`
2. Open two terminals, say (A) and (B)
3. Download [jvm-profiling-tools/async-profiler](https://github.com/jvm-profiling-tools/async-profiler) (version 2.0).
4. Change the current directory of (A) to the extracted one from async-profiler
5. Run `cd kohomology/profile; ./gradlew run` in (B)
6. Run `./profiler.sh -d 60 -f out.html -I '*KohomologyProfileKt*' -o flamegraph --minwidth 1.0 $(jps | \grep KohomologyProfileKt | awk '{print $1}')` in (A)
    - Note: `\` is added before `grep` in order to avoid calling alias
    - `'*Executable.main*'` may be more useful than `'*KohomologyProfileKt*'`
7. Press `ENTER` in (B)

### Generate documentation
`./gradlew dokkaHtml`

### Generate componentN
`./gradlew generateComponentN` will generate `util/list/componentN.kt` and its test.
The task is defined in `buildSrc`.

### Memo
#### Recursive generics
今はやめたけど，当初は `interface Scalar<S : Scalar<S>>` みたいに再帰的な定義をしてた．
ちゃんと安定して動作するのか不安だったけど，以下のような使用例があるので大丈夫っぽい．

- [kotlin/Enum.kt at master · JetBrains/kotlin](https://github.com/JetBrains/kotlin/blob/master/core/builtins/native/kotlin/Enum.kt) `Enum` でも使われている (`interface` じゃなくて `abstract class` だけど)
- [Kotlin 1.6.0 Released | The Kotlin Blog](https://blog.jetbrains.com/kotlin/2021/11/kotlin-1-6-0-is-released/) kotlin 1.6 で "Improved type inference for recursive generic types" という更新が行われている．


参考になりそうなリンクたちを列挙しておく．
(どれも長いのであんまりちゃんと読んでない)
- [Self Types - Language Design - Kotlin Discussions](https://discuss.kotlinlang.org/t/self-types/371/21)
- [Self Types with Java's Generics - SitePoint](https://www.sitepoint.com/self-types-with-javas-generics/)
- [Emulating self types in Kotlin. DIY solution for missing language… | by Jerzy Chałupski | Medium](https://medium.com/@jerzy.chalupski/emulating-self-types-in-kotlin-d64fe8ea2e62)

#### Why 'context'?
recursive generics をやめて `*.context.run {}` と書くようにした理由

- recursive generics でやろうとすると，2項演算(e.g. `plus`)の引数を対称的に扱えない．
  例えば `Vector.plus` だと `this.vectorSpace` と `other.vectorSpace` のどちらか一方を選択する必要がある．(もちろんその2つの一致を確認するので機能としては問題ないが，見た目として違和感がある)
- `Int.time(other: Vector)` のような extension method が書き易い．
  `context` 内では常に extension method が有効となるようにできるけど，他の方法だと一つ一つ `import` する必要があって面倒(だし，すぐに忘れる)．
- (extension method の話と重複するけれど) `d(gVector)` みたいな略記を導入しやすい．
- algebra の元に対する `a*b` を状況に応じて「通常の積」と「交換子による Lie bracket」で使い分けられる(けど，それは紛らわしいかも…？)

#### Version of java
native 向けにコンパイルしようとしたら，以下のエラーが出た．
依存関係のダウンロードをする際に，証明書関係で失敗しているっぽい．
使用する java のバージョンを変えたらうまくいった．

- `10.0.2-open`: NG
- `10.0.2-zulu`: OK

```
Downloading native dependencies (LLVM, sysroot etc). This is a one-time action performed only on the first run of the compiler.
Cannot download a dependency: javax.net.ssl.SSLHandshakeException: sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
```

## website
### スマホでのテスト
(`package.json` の `scripts` で `"start": "docusaurus start --host 0.0.0.0"` と設定しておく)

1. `hostname -I` で ip アドレスを調べる (`192.168.1.8` みたいなローカルなアドレスが出るはず？)
2. `npm run start`
3. スマホから `192.168.1.8:8080` にアクセス

### katex in 'overflow: scroll'
`overflow: scroll` を設定した要素の中に katex 数式を置くと表示がおかしくなった．
原因は `class="katex-mathml"` なる `span` 要素が `position: absolute` だったこと．
katex の設定で `{ output: "html" }` とすることでこの要素の出力を抑制できて，表示も正しくなる．

### ライブラリ本体と同一リポジトリに置いた理由
- `kohomology-js` は `kohomology` の SNAPSHOT 版を用いている
- benchmark 結果も website に載せる

### MyCodeBlock について
当初は(行儀が良くないとは認識しながら)
docusaurus の `CodeBlock` のソースコードをコピペして編集していたけど，
2022/4/13 に以下の理由でやめた．

docusaurus を `2.0.0-beta.9` から `2.0.0-beta.18` にアプデした際に問題が起きた．
`@docusaurus/theme-classic/src/theme/CodeBlock/index.tsx` をそのまま `MyCodeBlock.tsx` にコピペしても，
何故か kotlin のシンタックスハイライトが働かないという現象に遭遇．
原因はよく分からないけど，
kotlin が prism-react-renderer にビルトインで入っていないのが関係しているかも…？
(`docusaurus.config.js` で `additionalLanguages` として追加している)

# Sparse row echelon form optimization notes

## Scope

このメモでは、疎行列の階段形計算を高速化するための実装方針をまとめる。

扱う範囲は行列操作そのものに限定する。

- 並列化は考えない。
- cohomology 計算など、上位レイヤーの処理順序やタスク分割は考えない。
- 既存の `SparseRowEchelonFormCalculator` 系の外部 API はできるだけ維持する。
- 内部実装は複雑になってもよい。
- テストは十分にある前提で、実装の複雑さより性能改善の余地を重視する。

## Current bottleneck hypothesis

現在の実装は概ね row-oriented な `Map<Int, Map<Int, S>>` / `MutableMap<Int, S>` を中心にしている。
この表現は単純で扱いやすい一方で、疎行列の掃き出しでは次の点が弱い。

- pivot 列に非零成分を持つ行を探すために、行全体を走査しやすい。
- pivot 選択が単純で、fill-in を抑える情報を使っていない。
- `MutableMap<Int, S>` の汎用的な lookup/update/remove の overhead が大きい。
- 行更新のたびに非零成分数や列方向の情報を効率よく追跡できない。

そのため、小手先の最適化よりも、階段形計算専用の内部表現を作る方が有望だと思われる。

## Main proposal: sparse elimination engine with row and column indices

本命は、行方向と列方向の両方の index を持つ sparse elimination engine を新しく作ること。

内部状態は概念的には次のように持つ。

```text
rows: rowIndex -> mutable sparse row
cols: colIndex -> mutable set of rowIndex
rowNonZeroCount: rowIndex -> count
colNonZeroCount: colIndex -> count
```

`rows` は実際の行データを保持する。
`cols` は「ある列に非零成分を持つ行」を即座に列挙するために使う。
`rowNonZeroCount` と `colNonZeroCount` は pivot 選択や fill-in 推定に使う。

行更新で成分が変わるたびに、以下を同期して更新する。

- 0 でなかった成分が 0 になった場合:
  - row から列を削除する。
  - `cols[col].remove(rowIndex)` する。
  - `rowNonZeroCount` と `colNonZeroCount` を減らす。
- 0 だった成分が非零になった場合:
  - row に列を追加する。
  - `cols[col].add(rowIndex)` する。
  - `rowNonZeroCount` と `colNonZeroCount` を増やす。
- 非零成分が別の非零値に変わった場合:
  - index は変更せず、値だけ更新する。

これにより、pivot 列の対象行列挙を

```text
cols[pivotCol]
```

から行えるようになる。現在のように全行を見て `row[pivotCol] != null` を調べる処理を避けられる。

## Pivot selection

次に重要なのは pivot の選び方。
単純に最初に見つかった非零成分を pivot にすると、途中の fill-in が大きくなる可能性がある。

候補として Markowitz 型の pivot 選択を検討する。

```text
score(row, col) = (nnz(row) - 1) * (nnz(col) - 1)
```

この score は、その pivot を使ったときに新しく発生し得る fill-in の粗い上界として使える。
score が小さい pivot を選ぶことで、中間行列の非零成分数を抑えられる可能性がある。

完全に全非零成分から最小 score を探すと重いので、実装では近似でよい。

候補:

- 未処理列のうち、`colNonZeroCount` が小さい列から調べる。
- その列に属する行だけを候補にする。
- `rowNonZeroCount` と `colNonZeroCount` から score を計算する。
- 十分小さい score が見つかったら探索を打ち切る。
- 探索対象列数や候補数に上限を設ける。

有理数係数では fill-in が増えると分子分母のサイズも大きくなりやすい。
そのため、fill-in を減らす pivot 戦略は、非零成分数の削減以上に効く可能性がある。

## Elimination flow

新しい engine の基本的な流れは次の通り。

```text
while pivot が選べる:
    pivot 候補を選ぶ
    必要なら行交換する
    pivot 行と pivot 列を確定する
    cols[pivotCol] から消去対象行を列挙する
    各対象行に pivot 行の倍数を足し引きする
    rows / cols / nnz counts を同期更新する
```

重要なのは、対象行の列挙に column index を使うこと。
これにより、疎な pivot 列では不要な行走査を大きく減らせる。

## Sparse row representation

`MutableMap<Int, S>` は実装しやすいが、掃き出しの inner loop には重い可能性がある。
ただし、row 表現の変更は影響が大きいので、段階的に進めるのがよい。

### Step 1: keep mutable map internally

最初は `MutableMap<Int, S>` ベースの row のまま、新しい row/column index engine を作る。
これにより、column index と pivot 戦略の効果を先に測れる。

この段階では、row 更新専用 API を用意する。

```text
subtractMultiple(targetRow, pivotRow, scalar)
```

この API の中で、値の更新と column index の同期をまとめて行う。

### Step 2: introduce a dedicated sparse row abstraction

次に、row の実装を差し替えられるようにする。

```text
SparseMutableRow<S>
```

のような内部 interface / class を用意し、少なくとも次の操作を持たせる。

```text
get(col)
set(col, value)
remove(col)
entries()
nonZeroCount
subtractMultiple(other, scalar, onEntryChanged)
```

`onEntryChanged` のような callback で column index 更新を engine 側に通知する。

### Step 3: evaluate faster row implementations

候補:

- hash map based row
- sorted array based row
- small sorted array + large hash map の hybrid row
- JVM 限定なら primitive key map

sorted array は pivot 行との merge による更新が速くなる可能性があるが、fill-in で新しい列が増える場合に再構築が必要になる。
hybrid row は実装が複雑だが、疎行列では有望。

まずは column index と pivot 戦略の効果を確認してから、row 表現を差し替える方がよい。

## Compatibility notes

pivot 選択を変えると、通常の row echelon form は既存実装と同じ形にはならない可能性がある。
row echelon form は一般には一意でないため、これは自然な挙動。

影響を受けやすいテスト:

- 具体的な row echelon form の行や pivot 行を直接比較しているテスト
- 行交換回数を厳密に期待しているテスト
- 中間的な sparse row map の形を固定しているテスト

一方で、以下は数学的には一致するべき。

- rank
- determinant
- reduced row echelon form
- kernel
- image
- preimage
- 解空間

reduced row echelon form は体上では一意なので、最終結果の比較には向いている。

determinant については、pivot 選択や行交換が変わるため、符号と pivot 値の積の管理を注意深く行う必要がある。

## Proposed implementation order

1. 新しい calculator class を追加する。
   - 既存実装は維持する。
   - 例: `IndexedSparseRowEchelonFormCalculator`
   - 外部 API への露出方法は既存の `SparseMatrixSpace` の factory と合わせる。

2. `SparseEliminationEngine` を内部 class として作る。
   - `rows`
   - `cols`
   - `rowNonZeroCount`
   - `colNonZeroCount`
   - pivot 選択
   - 行更新
   - index 同期

3. 最初は `MutableMap<Int, S>` row のまま実装する。
   - 正しさと column index の効果を確認する。
   - 既存実装との比較テストを追加する。

4. pivot 選択はまず単純なものから始める。
   - 既存に近い pivot 選択で engine の正しさを確認する。
   - その後、Markowitz 近似を追加する。

5. operation measurement を追加または拡張する。
   - 行数
   - 列数
   - 初期非零成分数
   - 最大非零成分数
   - pivot ごとの対象行数
   - fill-in 数
   - pivot 選択 score
   - 計算時間

6. 必要なら row 表現を専用化する。
   - `SparseMutableRow` 抽象を入れる。
   - hash map row と sorted/hybrid row を比較できる形にする。

## Open questions

- 既存テストは row echelon form の具体的な形をどの程度固定しているか。
- determinant 計算で必要な行交換回数と pivot 値の管理を、どの class に持たせるべきか。
- column index の同期コストが、対象行探索の削減を上回るケースはどの程度あるか。
- Markowitz 近似の探索範囲をどの程度にするべきか。
- JVM 以外でも同じ実装を使うか、JVM 限定の高速実装にするか。

## Summary

並列化や上位レイヤーの変更を除外して行列操作だけを高速化するなら、最も有望なのは column index を持つ sparse elimination engine を作ること。

その上で Markowitz 型の pivot 選択を導入し、fill-in を抑える。
まずは `MutableMap` ベースで engine と pivot 戦略の効果を確認し、その後必要なら row 表現を sorted/hybrid などに差し替える。

この方針は実装量は大きいが、計算量そのものを減らす可能性があり、単なる inner loop の微修正よりも本質的な改善が期待できる。

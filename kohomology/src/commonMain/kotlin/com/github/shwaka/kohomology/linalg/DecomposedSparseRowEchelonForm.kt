package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.util.UnionFind
import com.github.shwaka.kohomology.util.list.hasNonEmptyIntersection
import com.github.shwaka.parautil.pmap

internal class DecomposedSparseRowEchelonForm<S : Scalar>(
    matrixSpace: AbstractSparseMatrixSpace<S>,
    originalMatrix: SparseMatrix<S>
) : RowEchelonForm<S, SparseNumVector<S>, SparseMatrix<S>>(matrixSpace, originalMatrix) {
    private val rowCount = originalMatrix.rowCount
    private val colCount = originalMatrix.colCount
    private val field = matrixSpace.field
    private val calculator = InPlaceSparseRowEchelonFormCalculator(field)
    private val data: SparseRowEchelonFormData<S> by lazy {
        this.computeData()
    }
    private val dataList: List<SparseRowEchelonFormData<S>> by lazy {
        this.computeDataList()
    }

    private fun computeData(): SparseRowEchelonFormData<S> {
        val dataList: List<SparseRowEchelonFormData<S>> = this.dataList
        val pivots: List<Int> = this.computePivots(dataList)
        val rowMap: Map<Int, Map<Int, S>> = this.computeRowMapForRowEchelonForm(dataList, pivots)
        val exchangeCount = 0 // not implemented
        return SparseRowEchelonFormData(rowMap, pivots, exchangeCount)
    }

    private fun computeDataList(): List<SparseRowEchelonFormData<S>> {
        return this.computeBlockList().pmap { block ->
            this.calculator.rowEchelonForm(block, this.colCount)
        }
    }

    private fun computeBlockList(): List<Map<Int, Map<Int, S>>> {
        val originalRowMap = this.originalMatrix.rowMap
        // rowIndices と rowKeysList を同じ方法で sort しておく必要がある。
        val rowIndices = originalRowMap.keys.toList().sorted()
        val rowKeysList: List<List<Int>> =
            originalRowMap
                .map { (rowInd, row) -> Pair(rowInd, row.keys.toList().sorted()) }
                .sortedBy { (rowInd, _) -> rowInd }
                .map { (_, rowKeys) -> rowKeys }
        val pairsToBeUnited: List<Pair<Int, Int>> = rowKeysList.withIndex().pmap { (i, rowKeys1) ->
            // 外側で定義した MutableList に複数のスレッドから add すると、ランダムに成功/失敗する。
            // MutableList.add() がスレッドセーフでないのが原因っぽい。
            val pairsToBeUnitedThreadLocal = mutableListOf<Pair<Int, Int>>()
            for (j in i + 1 until rowKeysList.size) {
                val rowKeys2 = rowKeysList[j]
                if (rowKeys1.hasNonEmptyIntersection(rowKeys2)) {
                    // unionFind.same(i, j) で先にチェックするとむしろ遅くなった。(並列処理とは無関係に)
                    // 原因は多分、same(i, j) == true となる割合が少なすぎること。
                    // if (unionFind.same(i, j)) {
                    //     continue
                    // }
                    pairsToBeUnitedThreadLocal.add(Pair(i, j))
                    // ↓スレッドセーフじゃない
                    // unionFind.unite(i, j)
                }
            }
            pairsToBeUnitedThreadLocal
        }.flatten()
        val unionFind = UnionFind(rowKeysList.size)
        for ((i, j) in pairsToBeUnited) {
            unionFind.unite(i, j)
        }
        return unionFind.groups().map { rowIndicesInNonZero ->
            rowIndicesInNonZero.map { i ->
                val rowInd = rowIndices[i]
                val row = originalRowMap[rowInd] ?: throw Exception("This can't happen!")
                Pair(rowInd, row)
            }.toMap()
        }
    }

    private fun computePivots(dataList: List<SparseRowEchelonFormData<S>>): List<Int> {
        return dataList.fold(emptyList<Int>()) { acc, data ->
            acc + data.pivots
        }.sorted()
    }

    private fun computeRowMapForRowEchelonForm(
        dataList: List<SparseRowEchelonFormData<S>>,
        pivots: List<Int>
    ): Map<Int, Map<Int, S>> {
        val rowMap: MutableMap<Int, Map<Int, S>> = mutableMapOf()
        for (data in dataList) {
            for ((rowIndInBlock, row) in data.rowMap) {
                val pivot = data.pivots[rowIndInBlock]
                val rowInd = pivots.indexOf(pivot)
                if (rowInd == -1)
                    throw Exception("This can't happen!")
                rowMap[rowInd] = row
            }
        }
        return rowMap
    }

    private fun computeReducedRowMapForRowEchelonForm(
        dataList: List<SparseRowEchelonFormData<S>>,
        pivots: List<Int>
    ): Map<Int, Map<Int, S>> {
        val rowMap: MutableMap<Int, Map<Int, S>> = mutableMapOf()
        for (data in dataList) {
            val reducedRowMapOfData = this.calculator.reduce(data.rowMap, data.pivots)
            for ((rowIndInBlock, row) in reducedRowMapOfData) {
                val pivot = data.pivots[rowIndInBlock]
                val rowInd = pivots.indexOf(pivot)
                if (rowInd == -1)
                    throw Exception("This can't happen!")
                rowMap[rowInd] = row
            }
        }
        return rowMap
    }

    override fun computeRowEchelonForm(): SparseMatrix<S> {
        return this.matrixSpace.fromRowMap(this.data.rowMap, this.rowCount, this.colCount)
    }

    override fun computePivots(): List<Int> {
        return this.data.pivots
    }

    override fun computeSign(): Sign {
        // return if (this.data.exchangeCount % 2 == 0) 1 else -1
        throw NotImplementedError("Computation of sign is not implemented for DecomposedSparseRowEchelonForm")
    }

    override fun computeReducedRowEchelonForm(): SparseMatrix<S> {
        // val reducedRowMap = this.calculator.reduce(this.data.rowMap, this.data.pivots)
        val reducedRowMap = this.computeReducedRowMapForRowEchelonForm(
            this.dataList,
            this.computePivots(),
        )
        return this.matrixSpace.fromRowMap(reducedRowMap, this.rowCount, this.colCount)
    }
}

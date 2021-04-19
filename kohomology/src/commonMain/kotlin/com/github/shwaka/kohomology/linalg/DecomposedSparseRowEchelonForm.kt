package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.util.UnionFind
import com.github.shwaka.kohomology.util.pmap

class DecomposedSparseRowEchelonForm<S : Scalar>(
    matrixSpace: AbstractSparseMatrixSpace<S>,
    originalMatrix: SparseMatrix<S>
) : RowEchelonForm<S, SparseNumVector<S>, SparseMatrix<S>>(matrixSpace, originalMatrix) {
    private val rowCount = originalMatrix.rowCount
    private val colCount = originalMatrix.colCount
    private val field = matrixSpace.field
    private val calculator = SparseRowEchelonFormCalculator(field)
    private val data: SparseRowEchelonFormData<S> by lazy {
        this.computeData()
    }

    private fun computeData(): SparseRowEchelonFormData<S> {
        val originalRowMap = this.originalMatrix.rowMap
        val rowIndices = originalRowMap.keys.toList()
        val unionFind = UnionFind(rowIndices.size)
        for ((i, rowInd1) in rowIndices.withIndex()) {
            for (j in i + 1 until rowIndices.size) {
                val rowInd2 = rowIndices[j]
                if (originalRowMap[rowInd1]!!.keys.intersect(originalRowMap[rowInd2]!!.keys).isNotEmpty()) {
                    unionFind.unite(i, j)
                }
            }
        }
        val blockList: List<Map<Int, Map<Int, S>>> = unionFind.groups().map { rowIndicesInNonZero ->
            rowIndicesInNonZero.map { i ->
                val rowInd = rowIndices[i]
                val row = originalRowMap[rowInd] ?: throw Exception("This can't happen!")
                Pair(rowInd, row)
            }.toMap()
        }
        val dataList = blockList.pmap { block -> this.calculator.rowEchelonForm(block, this.colCount) }
        val pivots = dataList.fold(emptyList<Int>()) { acc, data ->
            acc + data.pivots
        }.sorted()
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
        val exchangeCount = 0
        return SparseRowEchelonFormData(rowMap, pivots, exchangeCount)
    }

    override fun computeRowEchelonForm(): SparseMatrix<S> {
        return this.matrixSpace.fromRowMap(this.data.rowMap, this.rowCount, this.colCount)
    }

    override fun computePivots(): List<Int> {
        return this.data.pivots
    }

    override fun computeSign(): Sign {
        return if (this.data.exchangeCount % 2 == 0) 1 else -1
    }

    override fun computeReducedRowEchelonForm(): SparseMatrix<S> {
        val reducedRowMap = this.calculator.reduce(this.data.rowMap, this.data.pivots)
        return this.matrixSpace.fromRowMap(reducedRowMap, this.rowCount, this.colCount)
    }
}

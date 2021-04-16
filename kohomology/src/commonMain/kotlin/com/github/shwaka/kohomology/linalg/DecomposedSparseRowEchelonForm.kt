package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.util.Sign
import com.github.shwaka.kohomology.util.UnionFind

class DecomposedSparseRowEchelonForm<S : Scalar>(
    matrixSpace: SparseMatrixSpace<S>,
    originalMatrix: SparseMatrix<S>
) : RowEchelonForm<S, SparseNumVector<S>, SparseMatrix<S>>(matrixSpace, originalMatrix) {
    private val rowCount = originalMatrix.rowCount
    private val colCount = originalMatrix.colCount
    private val field = matrixSpace.field
    private val calculator = SparseRowEchelonFormCalculator(field)
    private val data: SparseRowEchelonFormData<S> by lazy {
        this.calculator.rowEchelonForm(this.originalMatrix.rowMap, this.colCount)
    }

    private fun computeData(): SparseRowEchelonFormData<S> {
        val unionFind = UnionFind(this.rowCount)
        val rowMap = this.originalMatrix.rowMap
        val rowIndices = rowMap.keys.toList()
        for ((i, rowInd1) in rowIndices.withIndex()) {
            for (j in i + 1 until rowIndices.size) {
                val rowInd2 = rowIndices[j]
                if (rowMap[rowInd1]!!.keys.intersect(rowMap[rowInd2]!!.keys).isNotEmpty()) {
                    unionFind.unite(rowInd1, rowInd2)
                }
            }
        }
        val blockList: List<Map<Int, Map<Int, S>>> = unionFind.groups().map { rowIndicesInBlock ->
            rowIndicesInBlock.map { rowInd -> Pair(rowInd, rowMap[rowInd]!!) }.toMap()
        }
        val dataList = blockList.map { block -> this.calculator.rowEchelonForm(block, this.colCount) }
        TODO("implementing")
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

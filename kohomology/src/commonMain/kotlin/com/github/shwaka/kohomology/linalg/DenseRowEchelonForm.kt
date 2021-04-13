package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.util.Sign

class DenseRowEchelonForm<S : Scalar>(
    matrixSpace: DenseMatrixSpace<S>,
    originalMatrix: DenseMatrix<S>
) : RowEchelonForm<S, DenseNumVector<S>, DenseMatrix<S>>(matrixSpace, originalMatrix) {
    private val data: RowEchelonFormData<S> by lazy { this.matrixSpace.context.run { this@DenseRowEchelonForm.originalMatrix.toList().rowEchelonForm() } }
    private val field: Field<S> = matrixSpace.field
    override fun computeRowEchelonForm(): DenseMatrix<S> {
        return this.matrixSpace.fromRowList(this.data.matrix)
    }

    override fun computePivots(): List<Int> {
        return this.data.pivots
    }

    override fun computeSign(): Sign {
        return if (this.data.exchangeCount % 2 == 0) 1 else -1
    }

    override fun computeReducedRowEchelonForm(): DenseMatrix<S> {
        val rowCount = this.originalMatrix.rowCount
        val rank = this.pivots.size
        val rowEchelonMatrix = this.data.matrix
        val one = this.matrixSpace.field.one
        var rawReducedMatrix = (0 until rowCount).map { i ->
            val a = if (i < rank) rowEchelonMatrix[i][this.pivots[i]] else one
            this.field.context.run {
                rowEchelonMatrix[i] * a.inv()
            }
        }
        for (i in 0 until rank) {
            rawReducedMatrix = rawReducedMatrix.eliminateOtherRows(i, this.pivots[i])
        }
        return this.matrixSpace.fromRowList(rawReducedMatrix, colCount = this.originalMatrix.colCount)
    }

    data class RowEchelonFormData<S : Scalar>(val matrix: List<List<S>>, val pivots: List<Int>, val exchangeCount: Int)

    private fun List<List<S>>.rowEchelonForm(): RowEchelonFormData<S> {
        return this.rowEchelonFormInternal(0, listOf(), 0)
    }

    private fun List<List<S>>.rowEchelonFormInternal(
        currentColInd: Int,
        pivots: List<Int>,
        exchangeCount: Int
    ): RowEchelonFormData<S> {
        if (this.isEmpty()) {
            // 0 行の行列だった場合
            return RowEchelonFormData(this, emptyList(), 0)
        }
        if (currentColInd == this[0].size) {
            // 全ての列の処理が終わった場合
            return RowEchelonFormData(this, pivots, exchangeCount)
        }
        val rowInd: Int? = this.findNonZero(currentColInd, pivots.size)
        return if (rowInd == null) {
            this.rowEchelonFormInternal(currentColInd + 1, pivots, exchangeCount)
        } else {
            var newMatrix = this.eliminateOtherRows(rowInd, currentColInd)
            var newExchangeCount = exchangeCount
            if (rowInd != pivots.size) {
                newMatrix = newMatrix.exchangeRows(rowInd, pivots.size)
                newExchangeCount += 1
            }
            val newPivots = pivots + listOf(currentColInd)
            newMatrix.rowEchelonFormInternal(currentColInd + 1, newPivots, newExchangeCount)
        }
    }

    private fun List<List<S>>.exchangeRows(i1: Int, i2: Int): List<List<S>> {
        if (i1 == i2) throw IllegalArgumentException("Row numbers must be distinct")
        return this.indices.map { i ->
            when (i) {
                i1 -> this[i2]
                i2 -> this[i1]
                else -> this[i]
            }
        }
    }

    private operator fun List<S>.minus(other: List<S>): List<S> {
        return this@DenseRowEchelonForm.field.context.run {
            this@minus.zip(other).map { (a, b) -> a - b }
        }
    }

    private operator fun List<S>.times(other: S): List<S> {
        return this@DenseRowEchelonForm.field.context.run {
            this@times.map { a -> a * other }
        }
    }

    // private fun <S : Scalar> List<List<S>>.addToAnotherRow(from: Int, to: Int, scalar: S): List<List<S>> {
    //     if (from == to) throw IllegalArgumentException("Row numbers must be distinct")
    //     return this.indices.map { i ->
    //         when (i) {
    //             to -> this[to] + this[from] * scalar
    //             else -> this[i]
    //         }
    //     }
    // }

    // private fun <S : Scalar> List<List<S>>.multiplyScalarToRow(to: Int, scalar: S): List<List<S>> {
    //     if (scalar == scalar.field.zero) throw IllegalArgumentException("scalar must be non-zero")
    //     return this.indices.map { i ->
    //         when (i) {
    //             to -> this[to].map { a -> a * scalar }
    //             else -> this[i]
    //         }
    //     }
    // }

    private fun List<List<S>>.eliminateOtherRows(rowInd: Int, colInd: Int): List<List<S>> {
        if (this[rowInd][colInd].isZero())
            throw IllegalArgumentException("Cannot eliminate since the element at ($rowInd, $colInd) is zero")
        val scalarMatrix: List<List<S>> = this
        return this@DenseRowEchelonForm.field.context.run {
            scalarMatrix.indices.map { i ->
                when (i) {
                    rowInd -> scalarMatrix[rowInd]
                    else -> scalarMatrix[i] - scalarMatrix[rowInd] * (scalarMatrix[i][colInd] / scalarMatrix[rowInd][colInd])
                }
            }
        }
    }

    private fun List<List<S>>.findNonZero(colInd: Int, rowIndFrom: Int): Int? {
        for (i in rowIndFrom until this.size) {
            if (this[i][colInd].isNotZero())
                return i
        }
        return null
    }
}

package com.github.shwaka.kohomology.specific.f2

import com.github.shwaka.kohomology.linalg.RowEchelonForm
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.Sign

internal data class SetRowEchelonFormData<S : Scalar>(
    val rowSetMap: Map<Int, Set<Int>>,
    val pivots: List<Int>,
    val exchangeCount: Int,
)

internal class SetRowEchelonForm<S : Scalar>(
    matrixSpace: SetMatrixSpace<S>,
    originalMatrix: SetMatrix<S>,
) : RowEchelonForm<S, SetNumVector<S>, SetMatrix<S>>(matrixSpace, originalMatrix) {
    private val data: SetRowEchelonFormData<S> by lazy {
        val rowSetMap = this.matrixSpace.context.run {
            this@SetRowEchelonForm.originalMatrix.rowSetMap
        }
        this.rowEchelonForm(rowSetMap, this.originalMatrix.colCount)
    }

    override fun computeRowEchelonForm(): SetMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun computePivots(): List<Int> {
        TODO("Not yet implemented")
    }

    override fun computeSign(): Sign {
        TODO("Not yet implemented")
    }

    override fun computeReducedRowEchelonForm(): SetMatrix<S> {
        TODO("Not yet implemented")
    }

    private fun rowEchelonForm(matrix: Map<Int, Set<Int>>, colCount: Int): SetRowEchelonFormData<S> {
        return matrix.rowEchelonFormInternal(0, listOf(), 0, colCount)
    }

    private tailrec fun Map<Int, Set<Int>>.rowEchelonFormInternal(
        currentColInd: Int,
        pivots: List<Int>,
        exchangeCount: Int,
        colCount: Int,
    ): SetRowEchelonFormData<S> {
        TODO()
    }

    private fun Map<Int, Set<Int>>.exchangeRows(i1: Int, i2: Int): Map<Int, Set<Int>> {
        require(i1 != i2) { "Row numbers must be distinct" }
        return this.mapKeys { (i, _) ->
            when (i) {
                i1 -> i2
                i2 -> i1
                else -> i
            }
        }
    }

    private operator fun Set<Int>.minus(other: Set<Int>): Set<Int> {
        return this xor other
    }

    private operator fun Set<Int>.times(scalar: S): Set<Int> {
        return if (scalar.isZero()) {
            emptySet()
        } else {
            this
        }
    }

    private fun Map<Int, Set<Int>>.eliminateOtherRows(rowInd: Int, colInd: Int): Map<Int, Set<Int>> {
        TODO()
    }

    private fun Map<Int, Set<Int>>.findNonZero(colInd: Int, rowIndFrom: Int): Int? {
        TODO()
    }
}

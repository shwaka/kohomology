package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.util.Sign

internal class SparseRowEchelonForm<S : Scalar>(
    matrixSpace: AbstractSparseMatrixSpace<S>,
    originalMatrix: SparseMatrix<S>
) : RowEchelonForm<S, SparseNumVector<S>, SparseMatrix<S>>(matrixSpace, originalMatrix) {
    private val rowCount = originalMatrix.rowCount
    private val colCount = originalMatrix.colCount
    private val calculator = matrixSpace.rowEchelonFormCalculator
    private val data: SparseRowEchelonFormData<S> by lazy {
        this.calculator.rowEchelonForm(this@SparseRowEchelonForm.originalMatrix.rowMap, this.colCount)
    }
    override fun computeRowEchelonForm(): SparseMatrix<S> {
        return this.matrixSpace.fromRowMap(this.data.rowMap, this.rowCount, this.colCount)
    }

    override fun computePivots(): List<Int> {
        return this.data.pivots
    }

    override fun computeSign(): Sign {
        return Sign.fromParity(this.data.exchangeCount)
    }

    override fun computeReducedRowEchelonForm(): SparseMatrix<S> {
        val reducedRowMap = this.calculator.reduce(this.data.rowMap, this.data.pivots)
        return this.matrixSpace.fromRowMap(reducedRowMap, this.rowCount, this.colCount)
    }
}

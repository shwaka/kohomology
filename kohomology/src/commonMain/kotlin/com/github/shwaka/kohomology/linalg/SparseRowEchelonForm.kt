package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.linalg.echeloncalc.AugmentedTransformTrackingSparseRowEchelonFormCalculator
import com.github.shwaka.kohomology.linalg.echeloncalc.SparseRowEchelonFormData
import com.github.shwaka.kohomology.linalg.echeloncalc.TransformTrackingSparseRowEchelonFormCalculator
import com.github.shwaka.kohomology.util.Sign

internal class SparseRowEchelonForm<S : Scalar>(
    matrixSpace: AbstractSparseMatrixSpace<S>,
    originalMatrix: SparseMatrix<S>
) : RowEchelonForm<S, SparseNumVector<S>, SparseMatrix<S>>(matrixSpace, originalMatrix) {
    private val rowCount = originalMatrix.rowCount
    private val colCount = originalMatrix.colCount
    private val calculator = matrixSpace.sparseRowEchelonFormCalculator
    private val data: SparseRowEchelonFormData<S> by lazy {
        this.calculator.rowEchelonForm(this@SparseRowEchelonForm.originalMatrix.rowMap, this.colCount)
    }
    private val trackingCalculator: TransformTrackingSparseRowEchelonFormCalculator<S> by lazy {
        this.calculator as? TransformTrackingSparseRowEchelonFormCalculator<S>
            ?: AugmentedTransformTrackingSparseRowEchelonFormCalculator(
                baseCalculator = this.calculator,
                field = this.matrixSpace.field,
            )
    }
    private val dataWithTransformation: SparseRowEchelonFormData<S> by lazy {
        this.trackingCalculator.rowEchelonFormWithTransformation(
            matrix = this@SparseRowEchelonForm.originalMatrix.rowMap,
            rowCount = this.rowCount,
            colCount = this.colCount,
        )
    }
    private val reducedDataWithTransformation: SparseRowEchelonFormData<S> by lazy {
        this.trackingCalculator.reduceWithTransformation(this.dataWithTransformation)
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

    override fun computeTransformation(): SparseMatrix<S> {
        val transformationRowMap = this.dataWithTransformation.transformationRowMap
            ?: error("rowEchelonFormWithTransformation must return transformationRowMap")
        return this.matrixSpace.fromRowMap(transformationRowMap, this.rowCount, this.rowCount)
    }

    override fun computeReducedTransformation(): SparseMatrix<S> {
        val transformationRowMap = this.reducedDataWithTransformation.transformationRowMap
            ?: error("reduceWithTransformation must return transformationRowMap")
        return this.matrixSpace.fromRowMap(transformationRowMap, this.rowCount, this.rowCount)
    }
}

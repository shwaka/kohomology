package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.linalg.echeloncalc.TransformTrackingSparseRowEchelonFormCalculator
import com.github.shwaka.kohomology.linalg.echeloncalc.TransformTrackingSparseRowEchelonFormData

internal class TrackingSparseRowEchelonTransformationStrategy<S : Scalar>(
    private val matrixSpace: AbstractSparseMatrixSpace<S>,
    private val originalMatrix: SparseMatrix<S>,
    private val calculator: TransformTrackingSparseRowEchelonFormCalculator<S>,
) : RowEchelonTransformationStrategy<S, SparseNumVector<S>, SparseMatrix<S>> {
    private val rowCount = this.originalMatrix.rowCount
    private val colCount = this.originalMatrix.colCount

    private val dataWithTransformation: TransformTrackingSparseRowEchelonFormData<S> by lazy {
        this.calculator.rowEchelonFormWithTransformation(
            matrix = this.originalMatrix.rowMap,
            rowCount = this.rowCount,
            colCount = this.colCount,
        )
    }

    private val reducedDataWithTransformation: TransformTrackingSparseRowEchelonFormData<S> by lazy {
        this.calculator.reduceWithTransformation(this.dataWithTransformation)
    }

    override fun computeTransformation(): SparseMatrix<S> {
        return this.matrixSpace.fromRowMap(
            this.dataWithTransformation.transformationRowMap,
            this.rowCount,
            this.rowCount,
        )
    }

    override fun computeReducedTransformation(): SparseMatrix<S> {
        return this.matrixSpace.fromRowMap(
            this.reducedDataWithTransformation.transformationRowMap,
            this.rowCount,
            this.rowCount,
        )
    }
}

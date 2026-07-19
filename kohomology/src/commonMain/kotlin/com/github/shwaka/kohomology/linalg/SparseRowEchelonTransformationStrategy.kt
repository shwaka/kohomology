package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.linalg.echeloncalc.TransformTrackingSparseRowEchelonFormCalculator
import com.github.shwaka.kohomology.linalg.echeloncalc.TransformTrackingSparseRowEchelonFormData

internal interface RowEchelonTransformationStrategy<S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    fun computeTransformation(): M
    fun computeReducedTransformation(): M
}

internal class AugmentationRowEchelonTransformationStrategy<S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>,
    private val originalMatrix: M,
) : RowEchelonTransformationStrategy<S, V, M> {
    private val augmentedOriginalMatrix: M by lazy {
        val rowCount = this.originalMatrix.rowCount
        this.matrixSpace.context.run {
            listOf(
                this@AugmentationRowEchelonTransformationStrategy.originalMatrix,
                this@AugmentationRowEchelonTransformationStrategy.matrixSpace.getIdentity(rowCount),
            ).join()
        }
    }

    private val augmentedRowEchelonForm: RowEchelonForm<S, V, M> by lazy {
        this.matrixSpace.context.run {
            this@AugmentationRowEchelonTransformationStrategy.augmentedOriginalMatrix.rowEchelonForm
        }
    }

    override fun computeTransformation(): M {
        val originalColCount = this.originalMatrix.colCount
        val augmentedColCount = this.augmentedOriginalMatrix.colCount
        return this.matrixSpace.context.run {
            this@AugmentationRowEchelonTransformationStrategy.augmentedRowEchelonForm.matrix
                .colSlice(originalColCount until augmentedColCount)
        }
    }

    override fun computeReducedTransformation(): M {
        val originalColCount = this.originalMatrix.colCount
        val augmentedColCount = this.augmentedOriginalMatrix.colCount
        return this.matrixSpace.context.run {
            this@AugmentationRowEchelonTransformationStrategy.augmentedRowEchelonForm.reducedMatrix
                .colSlice(originalColCount until augmentedColCount)
        }
    }
}

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

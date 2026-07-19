package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.linalg.echeloncalc.SparseRowEchelonFormData
import com.github.shwaka.kohomology.linalg.echeloncalc.TransformTrackingSparseRowEchelonFormCalculator

internal interface SparseRowEchelonTransformationStrategy<S : Scalar> {
    fun computeTransformation(): SparseMatrix<S>
    fun computeReducedTransformation(): SparseMatrix<S>
}

internal class AugmentationSparseRowEchelonTransformationStrategy<S : Scalar>(
    private val matrixSpace: AbstractSparseMatrixSpace<S>,
    private val originalMatrix: SparseMatrix<S>,
) : SparseRowEchelonTransformationStrategy<S> {
    private val augmentedOriginalMatrix: SparseMatrix<S> by lazy {
        val rowCount = this.originalMatrix.rowCount
        this.matrixSpace.context.run {
            listOf(
                this@AugmentationSparseRowEchelonTransformationStrategy.originalMatrix,
                this@AugmentationSparseRowEchelonTransformationStrategy.matrixSpace.getIdentity(rowCount),
            ).join()
        }
    }

    private val augmentedRowEchelonForm: RowEchelonForm<S, SparseNumVector<S>, SparseMatrix<S>> by lazy {
        this.matrixSpace.context.run {
            this@AugmentationSparseRowEchelonTransformationStrategy.augmentedOriginalMatrix.rowEchelonForm
        }
    }

    override fun computeTransformation(): SparseMatrix<S> {
        val originalColCount = this.originalMatrix.colCount
        val augmentedColCount = this.augmentedOriginalMatrix.colCount
        return this.matrixSpace.context.run {
            this@AugmentationSparseRowEchelonTransformationStrategy.augmentedRowEchelonForm.matrix
                .colSlice(originalColCount until augmentedColCount)
        }
    }

    override fun computeReducedTransformation(): SparseMatrix<S> {
        val originalColCount = this.originalMatrix.colCount
        val augmentedColCount = this.augmentedOriginalMatrix.colCount
        return this.matrixSpace.context.run {
            this@AugmentationSparseRowEchelonTransformationStrategy.augmentedRowEchelonForm.reducedMatrix
                .colSlice(originalColCount until augmentedColCount)
        }
    }
}

internal class TrackingSparseRowEchelonTransformationStrategy<S : Scalar>(
    private val matrixSpace: AbstractSparseMatrixSpace<S>,
    private val originalMatrix: SparseMatrix<S>,
    private val calculator: TransformTrackingSparseRowEchelonFormCalculator<S>,
) : SparseRowEchelonTransformationStrategy<S> {
    private val rowCount = this.originalMatrix.rowCount
    private val colCount = this.originalMatrix.colCount

    private val dataWithTransformation: SparseRowEchelonFormData<S> by lazy {
        this.calculator.rowEchelonFormWithTransformation(
            matrix = this.originalMatrix.rowMap,
            rowCount = this.rowCount,
            colCount = this.colCount,
        )
    }

    private val reducedDataWithTransformation: SparseRowEchelonFormData<S> by lazy {
        this.calculator.reduceWithTransformation(this.dataWithTransformation)
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

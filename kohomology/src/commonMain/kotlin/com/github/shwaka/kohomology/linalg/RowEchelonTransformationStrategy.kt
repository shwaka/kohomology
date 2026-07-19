package com.github.shwaka.kohomology.linalg

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

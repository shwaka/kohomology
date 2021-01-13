package com.github.shwaka.kohomology.linalg

import com.github.shwaka.kohomology.field.Field
import com.github.shwaka.kohomology.field.Scalar

class DenseMatrix<S>(
    val values: List<List<Scalar<S>>>,
    override val matrixSpace: DenseMatrixSpace<S>
) : Matrix<S, DenseNumVector<S>, DenseMatrix<S>> {
    override fun plus(other: DenseMatrix<S>): DenseMatrix<S> {
        TODO("Not yet implemented")
    }

    override fun times(vector: DenseNumVector<S>): DenseNumVector<S> {
        val values = this.values.map { row ->
            row.zip(vector.values)
                .map { it.first * it.second }
                .reduce(Scalar<S>::plus)
        }
        // val dim = this.matrixSpace.rowCount
        // val vectorSpace = DenseNumVectorSpace.from(this.matrixSpace.field, dim)
        return vector.vectorSpace.get(values)
    }

    override fun unwrap(): DenseMatrix<S> {
        return this
    }
}

class DenseMatrixSpace<S>(
    override val field: Field<S>,
    override val rowCount: Int,
    override val colCount: Int
) : MatrixSpace<S, DenseNumVector<S>, DenseMatrix<S>> {
    override fun wrap(m: DenseMatrix<S>): Matrix<S, DenseNumVector<S>, DenseMatrix<S>> {
        return m
    }

    fun get(values: List<List<Scalar<S>>>): DenseMatrix<S> {
        return DenseMatrix(values, this)
    }
}

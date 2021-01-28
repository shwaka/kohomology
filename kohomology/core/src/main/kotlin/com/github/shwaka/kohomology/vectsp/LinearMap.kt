package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector

class LinearMap<B0, B1, S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>>(
    val source: VectorSpace<B0, S, V>,
    val target: VectorSpace<B1, S, V>,
    val matrix: M
) {
    init {
        if (this.matrix.colCount != this.source.dim)
            throw IllegalArgumentException("The number of columns of the representing matrix does not match the dimension of the source vector space")
        if (this.matrix.rowCount != this.target.dim)
            throw IllegalArgumentException("The number of rows of the representing matrix does not match the dimension of the target vector space")
    }

    operator fun invoke(vector: Vector<B0, S, V>): Vector<B1, S, V> {
        if (vector.vectorSpace != this.source)
            throw IllegalArgumentException("Invalid vector is given as an argument for a linear map")
        return Vector(this.matrix * vector.numVector, this.target)
    }
}

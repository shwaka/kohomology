package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

class LinearMap<B0, B1, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val matrixSpace: MatrixSpace<S, V, M>,
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
        return this.matrixSpace.context.run {
            Vector(this@LinearMap.matrix * vector.numVector, this@LinearMap.target)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as LinearMap<*, *, *, *, *>

        if (source != other.source) return false
        if (target != other.target) return false
        if (matrix != other.matrix) return false

        return true
    }

    fun isIsomorphism(): Boolean {
        return this.matrixSpace.context.run { this@LinearMap.matrix.isInvertible() }
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + target.hashCode()
        result = 31 * result + matrix.hashCode()
        return result
    }

    fun kernelBasis(): List<Vector<B0, S, V>> {
        val numVectorList = this.matrixSpace.context.run { this@LinearMap.matrix.computeKernelBasis() }
        return numVectorList.map { this.source.fromNumVector(it) }
    }

    fun imageGenerator(): List<Vector<B1, S, V>> {
        val numVectorList = this.matrix.toNumVectorList()
        return numVectorList.map { this.target.fromNumVector(it) }
    }

    companion object {
        fun <B0, B1, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getZero(
            source: VectorSpace<B0, S, V>,
            target: VectorSpace<B1, S, V>,
            matrixSpace: MatrixSpace<S, V, M>
        ): LinearMap<B0, B1, S, V, M> {
            return LinearMap(matrixSpace, source, target, matrixSpace.getZero(source.dim, target.dim))
        }

        fun <B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getId(
            source: VectorSpace<B, S, V>,
            matrixSpace: MatrixSpace<S, V, M>
        ): LinearMap<B, B, S, V, M> {
            return LinearMap(matrixSpace, source, source, matrixSpace.getId(source.dim))
        }

        fun <B0, B1, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromMatrix(
            source: VectorSpace<B0, S, V>,
            target: VectorSpace<B1, S, V>,
            matrixSpace: MatrixSpace<S, V, M>,
            matrix: M
        ): LinearMap<B0, B1, S, V, M> {
            return LinearMap(matrixSpace, source, target, matrix)
        }

        fun <B0, B1, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromVectors(
            source: VectorSpace<B0, S, V>,
            target: VectorSpace<B1, S, V>,
            matrixSpace: MatrixSpace<S, V, M>,
            vectors: List<Vector<B1, S, V>>
        ): LinearMap<B0, B1, S, V, M> {
            if (vectors.size != source.dim)
                throw IllegalArgumentException("The number of vectors must be the same as the dimension of the source vector space")
            for (vector in vectors) {
                if (vector.vectorSpace != target)
                    throw IllegalArgumentException("The vector space for each vector must be the same as the target vector space")
            }
            val numVectors = vectors.map { it.toNumVector() }
            val matrix = matrixSpace.fromNumVectors(numVectors, target.dim)
            return LinearMap(matrixSpace, source, target, matrix)
        }
    }
}

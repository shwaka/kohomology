package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.exception.InvalidSizeException
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

public class LinearMap<BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val matrixSpace: MatrixSpace<S, V, M>,
    public val source: VectorSpace<BS, S, V>,
    public val target: VectorSpace<BT, S, V>,
    public val matrix: M
) {
    init {
        if (this.matrix.colCount != this.source.dim)
            throw InvalidSizeException("The number of columns of the representing matrix does not match the dimension of the source vector space")
        if (this.matrix.rowCount != this.target.dim)
            throw InvalidSizeException("The number of rows of the representing matrix does not match the dimension of the target vector space")
    }

    public operator fun invoke(vector: Vector<BS, S, V>): Vector<BT, S, V> {
        if (vector !in this.source)
            throw IllegalArgumentException("Invalid vector is given as an argument for a linear map")
        val numVector = this.matrixSpace.context.run {
            this@LinearMap.matrix * vector.numVector
        }
        return this@LinearMap.target.fromNumVector(numVector)
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

    public fun isIsomorphism(): Boolean {
        return this.matrixSpace.context.run { this@LinearMap.matrix.isInvertible() }
    }

    override fun hashCode(): Int {
        var result = source.hashCode()
        result = 31 * result + target.hashCode()
        result = 31 * result + matrix.hashCode()
        return result
    }

    public fun kernelBasis(): List<Vector<BS, S, V>> {
        val numVectorList = this.matrixSpace.context.run { this@LinearMap.matrix.computeKernelBasis() }
        return numVectorList.map { this.source.fromNumVector(it) }
    }

    public fun imageBasis(): List<Vector<BT, S, V>> {
        val numVectorList = this.matrixSpace.context.run { this@LinearMap.matrix.computeImageBasis() }
        return numVectorList.map { this.target.fromNumVector(it) }
    }

    public fun imageGenerator(): List<Vector<BT, S, V>> {
        val numVectorList = this.matrix.toNumVectorList()
        return numVectorList.map { this.target.fromNumVector(it) }
    }

    public fun findPreimage(vector: Vector<BT, S, V>): Vector<BS, S, V>? {
        if (vector !in this.target)
            throw IllegalArgumentException("Invalid vector is given: $vector is not an element of ${this.target}")
        return this.matrixSpace.context.run {
            this@LinearMap.matrix.findPreimage(vector.numVector)
        }?.let { numVector ->
            this.source.fromNumVector(numVector)
        }
    }

    public companion object {
        public fun <BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getZero(
            source: VectorSpace<BS, S, V>,
            target: VectorSpace<BT, S, V>,
            matrixSpace: MatrixSpace<S, V, M>
        ): LinearMap<BS, BT, S, V, M> {
            return LinearMap(matrixSpace, source, target, matrixSpace.getZero(source.dim, target.dim))
        }

        public fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getId(
            source: VectorSpace<B, S, V>,
            matrixSpace: MatrixSpace<S, V, M>
        ): LinearMap<B, B, S, V, M> {
            return LinearMap(matrixSpace, source, source, matrixSpace.getId(source.dim))
        }

        public fun <BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromMatrix(
            source: VectorSpace<BS, S, V>,
            target: VectorSpace<BT, S, V>,
            matrixSpace: MatrixSpace<S, V, M>,
            matrix: M
        ): LinearMap<BS, BT, S, V, M> {
            return LinearMap(matrixSpace, source, target, matrix)
        }

        public fun <BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromVectors(
            source: VectorSpace<BS, S, V>,
            target: VectorSpace<BT, S, V>,
            matrixSpace: MatrixSpace<S, V, M>,
            vectors: List<Vector<BT, S, V>>
        ): LinearMap<BS, BT, S, V, M> {
            if (vectors.size != source.dim)
                throw InvalidSizeException("The number of vectors must be the same as the dimension of the source vector space")
            for (vector in vectors) {
                if (vector.vectorSpace != target)
                    throw IllegalArgumentException("The vector space for each vector must be the same as the target vector space")
            }
            val numVectors = vectors.map { it.toNumVector() }
            val matrix = matrixSpace.fromNumVectorList(numVectors, target.dim)
            return LinearMap(matrixSpace, source, target, matrix)
        }
    }
}

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
        require(this.matrix.colCount == this.source.dim) {
            "The matrix has ${this.matrix.colCount} columns, " +
                "but the source vector space has dimension ${this.source.dim}."
        }
        require(this.matrix.rowCount == this.target.dim) {
            "The matrix has ${this.matrix.rowCount} rows, " +
                "but the target vector space has dimension ${this.target.dim}."
        }
    }

    public operator fun invoke(vector: Vector<BS, S, V>): Vector<BT, S, V> {
        if (vector !in this.source)
            throw IllegalArgumentException("Invalid vector is given as an argument of LinearMap: $vector is not an element of ${this.source}")
        val numVector = this.matrixSpace.context.run {
            this@LinearMap.matrix * vector.numVector
        }
        return this@LinearMap.target.fromNumVector(numVector)
    }

    public operator fun plus(other: LinearMap<BS, BT, S, V, M>): LinearMap<BS, BT, S, V, M> {
        require(this.source == other.source) { "Linear maps with different sources cannot be added" }
        require(this.target == other.target) { "Linear maps with different targets cannot be added" }
        return LinearMap(
            this.matrixSpace,
            this.source,
            this.target,
            this.matrixSpace.context.run {
                this@LinearMap.matrix + other.matrix
            }
        )
    }

    public operator fun <BR : BasisName> times(other: LinearMap<BR, BS, S, V, M>): LinearMap<BR, BT, S, V, M> {
        require(other.target == this.source) {
            "Cannot composite linear maps since the source of $this and the target of $other are different"
        }
        return LinearMap(
            this.matrixSpace,
            other.source,
            this.target,
            this.matrixSpace.context.run {
                this@LinearMap.matrix * other.matrix
            }
        )
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

    public fun isZero(): Boolean {
        return this.matrix.isZero()
    }

    public fun isNotZero(): Boolean = !this.isZero()

    public fun isIdentity(): Boolean {
        return this.matrix.isIdentity()
    }

    public fun isNotIdentity(): Boolean = !this.isIdentity()

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

    public fun kernel(): SubVectorSpace<BS, S, V, M> {
        return SubVectorSpace(this.matrixSpace, this.source, generator = this.kernelBasis())
    }

    public fun imageBasis(): List<Vector<BT, S, V>> {
        val numVectorList = this.matrixSpace.context.run { this@LinearMap.matrix.computeImageBasis() }
        return numVectorList.map { this.target.fromNumVector(it) }
    }

    public fun imageGenerator(): List<Vector<BT, S, V>> {
        val numVectorList = this.matrix.toNumVectorList()
        return numVectorList.map { this.target.fromNumVector(it) }
    }

    public fun image(): SubVectorSpace<BT, S, V, M> {
        return SubVectorSpace(this.matrixSpace, this.target, generator = this.imageGenerator())
    }

    public fun imageContains(vector: Vector<BT, S, V>): Boolean {
        if (vector !in this.target)
            throw IllegalArgumentException("Invalid vector is given: $vector is not an element of ${this.target}")
        return (this.findPreimage(vector) != null)
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

    public fun induce(
        sourceSub: SubVectorSpace<BS, S, V, M>,
        targetSub: SubVectorSpace<BT, S, V, M>,
    ): LinearMap<SubBasis<BS, S, V>, SubBasis<BT, S, V>, S, V, M> {
        val basisLift: List<Vector<BS, S, V>> =
            sourceSub.getBasis().map { subVector: Vector<SubBasis<BS, S, V>, S, V> ->
                sourceSub.inclusion(subVector)
            }
        val vectors: List<Vector<SubBasis<BT, S, V>, S, V>> =
            basisLift.map { vector: Vector<BS, S, V> ->
                targetSub.retraction(this(vector))
            }
        return LinearMap.fromVectors(
            sourceSub,
            targetSub,
            this.matrixSpace,
            vectors,
        )
    }

    public fun induce(
        sourceQuot: QuotVectorSpace<BS, S, V, M>,
        targetQuot: QuotVectorSpace<BT, S, V, M>,
    ): LinearMap<QuotBasis<BS, S, V>, QuotBasis<BT, S, V>, S, V, M> {
        val basisLift: List<Vector<BS, S, V>> =
            sourceQuot.getBasis().map { quotVector: Vector<QuotBasis<BS, S, V>, S, V> ->
                sourceQuot.section(quotVector)
            }
        val vectors: List<Vector<QuotBasis<BT, S, V>, S, V>> =
            basisLift.map { vector: Vector<BS, S, V> ->
                targetQuot.projection(this(vector))
            }
        return LinearMap.fromVectors(
            sourceQuot,
            targetQuot,
            this.matrixSpace,
            vectors,
        )
    }

    public fun induce(
        sourceSubQuot: SubQuotVectorSpace<BS, S, V, M>,
        targetSubQuot: SubQuotVectorSpace<BT, S, V, M>,
    ): LinearMap<SubQuotBasis<BS, S, V>, SubQuotBasis<BT, S, V>, S, V, M> {
        val basisLift: List<Vector<BS, S, V>> =
            sourceSubQuot.getBasis().map { subQuotVector: Vector<SubQuotBasis<BS, S, V>, S, V> ->
                sourceSubQuot.section(subQuotVector)
            }
        val vectors: List<Vector<SubQuotBasis<BT, S, V>, S, V>> =
            basisLift.map { vector: Vector<BS, S, V> ->
                targetSubQuot.projection(this(vector))
            }
        return LinearMap.fromVectors(
            sourceSubQuot,
            targetSubQuot,
            this.matrixSpace,
            vectors,
        )
    }

    public companion object {
        public fun <BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getZero(
            source: VectorSpace<BS, S, V>,
            target: VectorSpace<BT, S, V>,
            matrixSpace: MatrixSpace<S, V, M>
        ): LinearMap<BS, BT, S, V, M> {
            return LinearMap(matrixSpace, source, target, matrixSpace.getZero(target.dim, source.dim))
        }

        public fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getIdentity(
            source: VectorSpace<B, S, V>,
            matrixSpace: MatrixSpace<S, V, M>
        ): LinearMap<B, B, S, V, M> {
            return LinearMap(matrixSpace, source, source, matrixSpace.getIdentity(source.dim))
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

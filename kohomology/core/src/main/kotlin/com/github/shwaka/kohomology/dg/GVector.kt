package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorContext
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations

typealias Degree = Int

sealed class GVectorOrZero<B, S : Scalar, V : NumVector<S>>

class ZeroGVector<B, S : Scalar, V : NumVector<S>> : GVectorOrZero<B, S, V>()

open class GVector<B, S : Scalar, V : NumVector<S>>(
    val vector: Vector<B, S, V>,
    val degree: Degree,
    val gVectorSpace: GVectorSpace<B, S, V>
) : GVectorOrZero<B, S, V>() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as GVector<*, *, *>

        if (vector != other.vector) return false
        if (degree != other.degree) return false
        if (gVectorSpace != other.gVectorSpace) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vector.hashCode()
        result = 31 * result + degree
        result = 31 * result + gVectorSpace.hashCode()
        return result
    }

    override fun toString(): String = this.vector.toString()

    fun toString(basisToString: (B) -> String): String = this.vector.toString(basisToString)
}

interface GVectorOperations<B, S : Scalar, V : NumVector<S>> {
    fun add(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V>
    fun subtract(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V>
    fun multiply(scalar: S, gVector: GVector<B, S, V>): GVector<B, S, V>
    val zeroGVector: ZeroGVector<B, S, V>
}

open class GVectorContext<B, S : Scalar, V : NumVector<S>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<B, S, V>,
) : NumVectorContext<S, V>(scalarOperations, numVectorOperations), GVectorOperations<B, S, V> by gVectorOperations {
    operator fun GVector<B, S, V>.plus(other: GVector<B, S, V>): GVector<B, S, V> = this@GVectorContext.add(this, other)
    operator fun GVector<B, S, V>.minus(other: GVector<B, S, V>): GVector<B, S, V> = this@GVectorContext.subtract(this, other)
    operator fun GVector<B, S, V>.times(scalar: S): GVector<B, S, V> = this@GVectorContext.multiply(scalar, this)
    operator fun S.times(gVector: GVector<B, S, V>): GVector<B, S, V> = this@GVectorContext.multiply(this, gVector)
    operator fun GVector<B, S, V>.times(scalar: Int): GVector<B, S, V> = this@GVectorContext.multiply(scalar.toScalar(), this)
    operator fun Int.times(gVector: GVector<B, S, V>): GVector<B, S, V> = this@GVectorContext.multiply(this.toScalar(), gVector)
    operator fun GVector<B, S, V>.unaryMinus(): GVector<B, S, V> = this@GVectorContext.multiply((-1).toScalar(), this)
}

open class GVectorSpace<B, S : Scalar, V : NumVector<S>>(
    val numVectorSpace: NumVectorSpace<S, V>,
    private val getBasisNames: (Degree) -> List<B>
) : GVectorOperations<B, S, V> {
    val field = this.numVectorSpace.field
    private val cache: MutableMap<Degree, VectorSpace<B, S, V>> = mutableMapOf()

    private val gVectorContext = GVectorContext(numVectorSpace.field, numVectorSpace, this)
    fun <T> withContext(block: GVectorContext<B, S, V>.() -> T) = this.gVectorContext.block()

    operator fun get(degree: Degree): VectorSpace<B, S, V> {
        // if cache exists
        this.cache[degree]?.let { return it }
        // if cache does not exist
        val basisNames: List<B> = this.getBasisNames(degree)
        val vectorSpace = VectorSpace(this.numVectorSpace, basisNames)
        this.cache[degree] = vectorSpace
        return vectorSpace
    }

    fun fromVector(vector: Vector<B, S, V>, degree: Degree): GVector<B, S, V> {
        return GVector(vector, degree, this)
    }

    fun fromNumVector(numVector: V, degree: Degree): GVector<B, S, V> {
        val vectorSpace = this[degree]
        val vector = Vector(numVector, vectorSpace)
        return this.fromVector(vector, degree)
    }

    fun fromCoeff(coeff: List<S>, degree: Degree): GVector<B, S, V> {
        val numVector = this.numVectorSpace.fromValues(coeff)
        return this.fromNumVector(numVector, degree)
    }

    fun getBasis(degree: Degree): List<GVector<B, S, V>> {
        return this[degree].getBasis().map { vector ->
            this.fromVector(vector, degree)
        }
    }

    fun getZero(degree: Degree): GVector<B, S, V> {
        val vector = this[degree].zeroVector
        return this.fromVector(vector, degree)
    }

    fun convertToGVector(gVectorOrZero: GVectorOrZero<B, S, V>, degree: Degree): GVector<B, S, V> {
        return when (gVectorOrZero) {
            is ZeroGVector -> this.getZero(degree)
            is GVector -> gVectorOrZero
        }
    }

    override fun add(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V> {
        if (a.gVectorSpace != this)
            throw ArithmeticException("The gVector $a does not match the context")
        if (b.gVectorSpace != this)
            throw ArithmeticException("The gVector $b does not match the context")
        if (a.degree != b.degree)
            throw ArithmeticException("Cannot add two graded vectors of different degrees")
        val vector = a.vector.vectorSpace.withContext {
            a.vector + b.vector
        }
        return this@GVectorSpace.fromVector(vector, a.degree)
    }

    override fun subtract(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V> {
        if (a.gVectorSpace != this)
            throw ArithmeticException("The gVector $a does not match the context")
        if (b.gVectorSpace != this)
            throw ArithmeticException("The gVector $b does not match the context")
        if (a.degree != b.degree)
            throw ArithmeticException("Cannot add two graded vectors of different degrees")
        val vector = a.vector.vectorSpace.withContext {
            a.vector - b.vector
        }
        return this@GVectorSpace.fromVector(vector, a.degree)
    }

    override fun multiply(scalar: S, gVector: GVector<B, S, V>): GVector<B, S, V> {
        if (gVector.gVectorSpace != this)
            throw ArithmeticException("The gVector $gVector does not match the context")
        val vector = gVector.vector.vectorSpace.withContext { scalar * gVector.vector }
        return this.fromVector(vector, gVector.degree)
    }

    override val zeroGVector: ZeroGVector<B, S, V> = ZeroGVector()
}

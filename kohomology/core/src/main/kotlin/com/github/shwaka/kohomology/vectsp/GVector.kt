package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar

typealias Degree = Int

sealed class GVectorOrZero<B, S : Scalar<S>, V : NumVector<S, V>>

class ZeroGVector<B, S : Scalar<S>, V : NumVector<S, V>> : GVectorOrZero<B, S, V>()

class GVector<B, S : Scalar<S>, V : NumVector<S, V>>(
    val vector: Vector<B, S, V>,
    val degree: Degree,
    val gVectorSpace: GVectorSpace<B, S, V>
) : GVectorOrZero<B, S, V>() {
    operator fun plus(other: GVector<B, S, V>): GVector<B, S, V> {
        if (this.gVectorSpace != other.gVectorSpace)
            throw ArithmeticException("Cannot add two graded vectors in different graded vector spaces")
        if (this.degree != other.degree)
            throw ArithmeticException("Cannot add two graded vectors of different degrees")
        return this.gVectorSpace.fromVector(this.vector + other.vector, this.degree)
    }

    operator fun minus(other: GVector<B, S, V>): GVector<B, S, V> {
        if (this.gVectorSpace != other.gVectorSpace)
            throw ArithmeticException("Cannot subtract two graded vectors in different graded vector spaces")
        if (this.degree != other.degree)
            throw ArithmeticException("Cannot subtract two graded vectors of different degrees")
        return this.gVectorSpace.fromVector(this.vector + other.vector, this.degree)
    }

    operator fun unaryMinus(): GVector<B, S, V> {
        return this.gVectorSpace.fromVector(-this.vector, this.degree)
    }

    operator fun times(scalar: S): GVector<B, S, V> {
        return this.gVectorSpace.fromVector(this.vector * scalar, this.degree)
    }

    operator fun times(scalar: Int): GVector<B, S, V> {
        return this.gVectorSpace.fromVector(this.vector * scalar, this.degree)
    }

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

class GVectorSpace<B, S : Scalar<S>, V : NumVector<S, V>>(
    val numVectorSpace: NumVectorSpace<S, V>,
    private val getBasisNames: (Degree) -> List<B>
) {
    val field = this.numVectorSpace.field
    private val cache: MutableMap<Degree, VectorSpace<B, S, V>> = mutableMapOf()

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
        val vector = this[degree].zero
        return this.fromVector(vector, degree)
    }

    fun convertToGVector(gVectorOrZero: GVectorOrZero<B, S, V>, degree: Degree): GVector<B, S, V> {
        return when (gVectorOrZero) {
            is ZeroGVector -> this.getZero(degree)
            is GVector -> gVectorOrZero
        }
    }
}

package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace

typealias Degree = Int

class GVector<B, S : Scalar<S>, V : NumVector<S, V>>(
    val vector: Vector<B, S, V>,
    val deg: Degree,
    val gVectorSpace: GVectorSpace<B, S, V>
) {
    operator fun plus(other: GVector<B, S, V>): GVector<B, S, V> {
        if (this.gVectorSpace != other.gVectorSpace)
            throw ArithmeticException("Cannot add two graded vectors in different graded vector spaces")
        if (this.deg != other.deg)
            throw ArithmeticException("Cannot add two graded vectors of different degrees")
        return GVector(this.vector + other.vector, this.deg, this.gVectorSpace)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as GVector<*, *, *>

        if (vector != other.vector) return false
        if (deg != other.deg) return false
        if (gVectorSpace != other.gVectorSpace) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vector.hashCode()
        result = 31 * result + deg
        result = 31 * result + gVectorSpace.hashCode()
        return result
    }
}

class GVectorSpace<B, S : Scalar<S>, V : NumVector<S, V>>(
    val numVectorSpace: NumVectorSpace<S, V>,
    private val getVectorSpace: (Degree) -> VectorSpace<B, S, V>
) {
    private val cache: MutableMap<Degree, VectorSpace<B, S, V>> = mutableMapOf()

    operator fun get(deg: Degree): VectorSpace<B, S, V> {
        // if cache exists
        this.cache[deg]?.let { return it }
        // if cache does not exist
        val vectorSpace = this.getVectorSpace(deg)
        this.cache[deg] = vectorSpace
        return vectorSpace
    }

    fun fromNumVector(numVector: V, deg: Degree): GVector<B, S, V> {
        val vectorSpace = this[deg]
        val vector = Vector(numVector, vectorSpace)
        return GVector(vector, deg, this)
    }

    fun fromCoeff(coeff: List<S>, deg: Degree): GVector<B, S, V> {
        val numVector = this.numVectorSpace.fromValues(coeff)
        return this.fromNumVector(numVector, deg)
    }
}

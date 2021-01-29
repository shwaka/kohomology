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

    fun fromNumVector(numVector: NumVector<S, V>, deg: Degree): GVector<B, S, V> {
        TODO("not impl")
    }

    fun fromCoeff(coeff: List<S>, deg: Degree): GVector<B, S, V> {
        TODO("not impl")
    }
}

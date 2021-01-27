package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace

class Vector<B, S : Scalar<S>, V : NumVector<S, V>>(private val numVector: V, val vectorSpace: VectorSpace<B, S, V>) {
    operator fun plus(other: Vector<B, S, V>): Vector<B, S, V> {
        return Vector(this.numVector + other.numVector, this.vectorSpace)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as Vector<*, *, *>

        if (numVector != other.numVector) return false
        if (vectorSpace != other.vectorSpace) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numVector.hashCode()
        result = 31 * result + vectorSpace.hashCode()
        return result
    }

    fun toString(basisToString: (B) -> String): String {
        val coeffList = this.numVector.toList()
        val basis = this.vectorSpace.basis.map(basisToString)
        return coeffList.zip(basis).joinToString(separator = " + ") { (coeff, basisElm) -> "$coeff $basisElm" }
    }

    override fun toString(): String {
        return this.toString { it.toString() }
    }
}

class VectorSpace<B, S : Scalar<S>, V : NumVector<S, V>>(
    val numVectorSpace: NumVectorSpace<S, V>,
    val dim: Int,
    val basis: List<B>
) {
    fun fromNumVector(numVector: V): Vector<B, S, V> {
        return Vector(numVector, this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (this::class != other::class) return false

        other as VectorSpace<*, *, *>

        if (numVectorSpace != other.numVectorSpace) return false
        if (dim != other.dim) return false
        if (basis != other.basis) return false

        return true
    }

    override fun hashCode(): Int {
        var result = numVectorSpace.hashCode()
        result = 31 * result + dim
        result = 31 * result + basis.hashCode()
        return result
    }
}

package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

data class BasisPair<B>(val first: B, val second: B) {
    private fun stringPairToString(s1: String, s2: String): String {
        return "($s1, $s2)"
    }
    override fun toString(): String {
        return this.stringPairToString(this.first.toString(), this.second.toString())
    }
    fun toString(basisToString: (B) -> String): String {
        return this.stringPairToString(basisToString(this.first), basisToString(this.second))
    }
}

class TensorProduct<B, S : Scalar<S>, V : NumVector<S, V>>(
    val vectorSpace1: VectorSpace<B, S, V>,
    val vectorSpace2: VectorSpace<B, S, V>
) {
    val vectorSpace: VectorSpace<BasisPair<B>, S, V>
    init {
        if (vectorSpace1.numVectorSpace != vectorSpace2.numVectorSpace)
            throw IllegalArgumentException("Tensor product of two vector spaces with different numerical vector spaces cannot be defined")
        val basisNames: List<BasisPair<B>> = vectorSpace1.basisNames.flatMap { b1 ->
            vectorSpace2.basisNames.map { b2 -> BasisPair(b1, b2) }
        }
        this.vectorSpace = VectorSpace(vectorSpace1.numVectorSpace, basisNames)
    }

    fun tensorProductOf(vector1: Vector<B, S, V>, vector2: Vector<B, S, V>): Vector<BasisPair<B>, S, V> {
        if (vector1.vectorSpace != this.vectorSpace1)
            throw IllegalArgumentException("The first vector is not an element of the first vector space")
        if (vector2.vectorSpace != this.vectorSpace2)
            throw IllegalArgumentException("The second vector is not an element of the second vector space")
        val coeffList = this.vectorSpace.basisNames.map { (basis1, basis2) ->
            val coeff1 = vector1.coeffOf(basis1)
            val coeff2 = vector2.coeffOf(basis2)
            this.vectorSpace.field.withContext {
                coeff1 * coeff2
            }
        }
        return this.vectorSpace.fromCoeff(coeffList)
    }

    infix fun Vector<B, S, V>.tensor(other: Vector<B, S, V>): Vector<BasisPair<B>, S, V> {
        return this@TensorProduct.tensorProductOf(this, other)
    }

    fun withContext(block: TensorProduct<B, S, V>.() -> Unit) {
        this.block()
    }
}

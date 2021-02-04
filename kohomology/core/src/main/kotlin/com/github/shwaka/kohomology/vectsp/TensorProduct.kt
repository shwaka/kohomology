package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.field.Scalar
import com.github.shwaka.kohomology.linalg.NumVector

data class BasisPair<B1, B2>(val first: B1, val second: B2) {
    private fun stringPairToString(s1: String, s2: String): String {
        return "(${s1}, ${s2})"
    }
    override fun toString(): String {
        return this.stringPairToString(this.first.toString(), this.second.toString())
    }
    fun toString(firstBasisToString: (B1) -> String, secondBasisToString: (B2) -> String): String {
        return this.stringPairToString(firstBasisToString(this.first), secondBasisToString(this.second))
    }
}

class TensorProduct<B1, B2, S : Scalar<S>, V : NumVector<S, V>>(
    val vectorSpace1: VectorSpace<B1, S, V>,
    val vectorSpace2: VectorSpace<B2, S, V>
) {
    val vectorSpace: VectorSpace<BasisPair<B1, B2>, S, V>
    init {
        if (vectorSpace1.numVectorSpace != vectorSpace2.numVectorSpace)
            throw IllegalArgumentException("Tensor product of two vector spaces with different numerical vector spaces cannot be defined")
        val basisNames: List<BasisPair<B1, B2>> = vectorSpace1.basisNames.flatMap { b1 ->
            vectorSpace2.basisNames.map { b2 -> BasisPair(b1, b2) }
        }
        this.vectorSpace = VectorSpace(vectorSpace1.numVectorSpace, basisNames)
    }

    fun tensorProductOf(vector1: Vector<B1, S, V>, vector2: Vector<B2, S, V>): Vector<BasisPair<B1, B2>, S, V> {
        if (vector1.vectorSpace != this.vectorSpace1)
            throw IllegalArgumentException("The first vector is not an element of the first vector space")
        if (vector2.vectorSpace != this.vectorSpace2)
            throw IllegalArgumentException("The second vector is not an element of the second vector space")
        val coeffList = this.vectorSpace.basisNames.map { (basis1, basis2) ->
            val coeff1 = vector1.coeffOf(basis1)
            val coeff2 = vector2.coeffOf(basis2)
            coeff1 * coeff2
        }
        return this.vectorSpace.fromCoeff(coeffList)
    }
}

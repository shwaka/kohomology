package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorContext
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

class TensorProductContext<B, S : Scalar<S>, V : NumVector<S, V>>(
    private val tensorProduct: TensorProduct<B, S, V>
) : NumVectorContext<S, V>(tensorProduct.vectorSpace.field, tensorProduct.vectorSpace.numVectorSpace) {
    @Suppress("UNCHECKED_CAST")
    operator fun <X> Vector<X, S, V>.plus(other: Vector<X, S, V>): Vector<X, S, V> {
        val vectorSpace = this@TensorProductContext.tensorProduct.vectorSpace
        val vectorSpace1 = this@TensorProductContext.tensorProduct.vectorSpace1
        val vectorSpace2 = this@TensorProductContext.tensorProduct.vectorSpace2
        return if (this.vectorSpace == vectorSpace && other.vectorSpace == vectorSpace) {
            this as Vector<BasisPair<B>, S, V>
            other as Vector<BasisPair<B>, S, V>
            vectorSpace.withContext { this@plus + other } as Vector<X, S, V>
        } else if (this.vectorSpace == vectorSpace1 && other.vectorSpace == vectorSpace1) {
            this as Vector<B, S, V>
            other as Vector<B, S, V>
            vectorSpace1.withContext { this@plus + other } as Vector<X, S, V>
        } else if (this.vectorSpace == vectorSpace2 && other.vectorSpace == vectorSpace2) {
            this as Vector<B, S, V>
            other as Vector<B, S, V>
            vectorSpace2.withContext { this@plus + other } as Vector<X, S, V>
        } else {
            throw ArithmeticException("The vector spaces ${this.vectorSpace} and ${other.vectorSpace} do not match the context")
        }
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <X> Vector<X, S, V>.minus(other: Vector<X, S, V>): Vector<X, S, V> {
        val vectorSpace = this@TensorProductContext.tensorProduct.vectorSpace
        val vectorSpace1 = this@TensorProductContext.tensorProduct.vectorSpace1
        val vectorSpace2 = this@TensorProductContext.tensorProduct.vectorSpace2
        return if (this.vectorSpace == vectorSpace && other.vectorSpace == vectorSpace) {
            this as Vector<BasisPair<B>, S, V>
            other as Vector<BasisPair<B>, S, V>
            vectorSpace.withContext { this@minus - other } as Vector<X, S, V>
        } else if (this.vectorSpace == vectorSpace1 && other.vectorSpace == vectorSpace1) {
            this as Vector<B, S, V>
            other as Vector<B, S, V>
            vectorSpace1.withContext { this@minus - other } as Vector<X, S, V>
        } else if (this.vectorSpace == vectorSpace2 && other.vectorSpace == vectorSpace2) {
            this as Vector<B, S, V>
            other as Vector<B, S, V>
            vectorSpace2.withContext { this@minus - other } as Vector<X, S, V>
        } else {
            throw ArithmeticException("The vector spaces ${this.vectorSpace} and ${other.vectorSpace} do not match the context")
        }
    }

    operator fun <X> Vector<X, S, V>.times(scalar: S): Vector<X, S, V> {
        val vectorSpace1 = this@TensorProductContext.tensorProduct.vectorSpace1
        val vectorSpace2 = this@TensorProductContext.tensorProduct.vectorSpace2
        if (scalar.field != this@TensorProductContext.field)
            throw ArithmeticException("The field ${scalar.field} does not match the context")
        return if (this.vectorSpace == vectorSpace) {
            vectorSpace.withContext { this@times * scalar }
        } else if (this.vectorSpace == vectorSpace1) {
            vectorSpace1.withContext { this@times * scalar }
        } else if (this.vectorSpace == vectorSpace2) {
            vectorSpace2.withContext { this@times * scalar }
        } else {
            throw ArithmeticException("The vector space ${this.vectorSpace} does not match the context")
        }
    }
    operator fun <X> S.times(vector: Vector<X, S, V>): Vector<X, S, V> = vector * this
    operator fun <X> Vector<X, S, V>.times(scalar: Int): Vector<X, S, V> = this * scalar.toScalar()
    operator fun <X> Int.times(vector: Vector<X, S, V>): Vector<X, S, V> = vector * this.toScalar()

    operator fun <X> Vector<X, S, V>.unaryMinus(): Vector<X, S, V> = this * (-1)

    infix fun Vector<B, S, V>.tensor(other: Vector<B, S, V>): Vector<BasisPair<B>, S, V> {
        return this@TensorProductContext.tensorProduct.tensorProductOf(this, other)
    }
}

class TensorProduct<B, S : Scalar<S>, V : NumVector<S, V>>(
    val vectorSpace1: VectorSpace<B, S, V>,
    val vectorSpace2: VectorSpace<B, S, V>
) {
    val vectorSpace: VectorSpace<BasisPair<B>, S, V>
    private val context by lazy { TensorProductContext(this) } // 直接代入するとなぜか Null Pointer Exception が起きる

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

    fun <T> withContext(block: TensorProductContext<B, S, V>.() -> T): T = this.context.block()
}

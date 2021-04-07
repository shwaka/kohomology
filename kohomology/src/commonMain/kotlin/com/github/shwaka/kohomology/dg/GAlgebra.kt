package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.util.Degree
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

interface GAlgebraOperations<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    fun multiply(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V>
    val unit: GVector<B, S, V>
}

class GAlgebraContext<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<B, S, V>,
    gAlgebraOperations: GAlgebraOperations<B, S, V, M>,
) : GVectorContext<B, S, V>(scalarOperations, numVectorOperations, gVectorOperations), GAlgebraOperations<B, S, V, M> by gAlgebraOperations {
    operator fun GVector<B, S, V>.times(other: GVector<B, S, V>): GVector<B, S, V> {
        return this@GAlgebraContext.multiply(this, other)
    }
    fun GVector<B, S, V>.pow(exponent: Int): GVector<B, S, V> {
        val unit = this@GAlgebraContext.unit
        return when {
            exponent == 0 -> unit
            exponent == 1 -> this
            exponent > 1 -> {
                val half = this.pow(exponent / 2)
                val rem = if (exponent % 2 == 1) this else unit
                half * half * rem
            }
            exponent < 0 -> throw ArithmeticException("Negative power in an algebra is not defined")
            else -> throw Exception("This can't happen!")
        }
    }
}

open class GAlgebra<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val matrixSpace: MatrixSpace<S, V, M>,
    name: String,
    getVectorSpace: (Degree) -> VectorSpace<B, S, V>,
    val getMultiplication: (Degree, Degree) -> BilinearMap<B, B, B, S, V, M>,
    unitVector: Vector<B, S, V>
) : GVectorSpace<B, S, V>(matrixSpace.numVectorSpace, name, getVectorSpace), GAlgebraOperations<B, S, V, M> {
    // use 'lazy' to avoid the following warning:
    //   Leaking 'this' in constructor of non-final class GAlgebra
    override val context by lazy {
        GAlgebraContext(matrixSpace.numVectorSpace.field, matrixSpace.numVectorSpace, this, this)
    }

    override val unit: GVector<B, S, V> = this.fromVector(unitVector, 0)

    private val multiplication: GBilinearMap<B, B, B, S, V, M> by lazy {
        GBilinearMap(this, this, this, 0) { p, q -> getMultiplication(p, q) }
    }
    override fun multiply(a: GVector<B, S, V>, b: GVector<B, S, V>): GVector<B, S, V> {
        return this.multiplication(a, b)
    }

    fun isBasis(
        gVectorList: List<GVector<B, S, V>>,
        degree: Degree,
    ): Boolean {
        return this.isBasis(gVectorList, degree, this.matrixSpace)
    }
}

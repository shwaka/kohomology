package com.github.shwaka.kohomology.dg

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.DefaultVectorPrinter
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorPrinter
import com.github.shwaka.kohomology.vectsp.VectorSpace

interface GAlgebraOperations<B : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    fun multiply(a: GVector<B, D, S, V>, b: GVector<B, D, S, V>): GVector<B, D, S, V>
    fun multiply(a: GVectorOrZero<B, D, S, V>, b: GVectorOrZero<B, D, S, V>): GVectorOrZero<B, D, S, V>
    val unit: GVector<B, D, S, V>
}

open class GAlgebraContext<B : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<B, D, S, V>,
    gAlgebraOperations: GAlgebraOperations<B, D, S, V, M>,
) : GVectorContext<B, D, S, V>(scalarOperations, numVectorOperations, gVectorOperations),
    GAlgebraOperations<B, D, S, V, M> by gAlgebraOperations {
    operator fun GVector<B, D, S, V>.times(other: GVector<B, D, S, V>): GVector<B, D, S, V> {
        return this@GAlgebraContext.multiply(this, other)
    }
    operator fun GVectorOrZero<B, D, S, V>.times(other: GVectorOrZero<B, D, S, V>): GVectorOrZero<B, D, S, V> {
        return this@GAlgebraContext.multiply(this, other)
    }
    fun GVector<B, D, S, V>.pow(exponent: Int): GVector<B, D, S, V> {
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
    fun GVectorOrZero<B, D, S, V>.pow(exponent: Int): GVectorOrZero<B, D, S, V> {
        return when (this) {
            is GVector -> this.pow(exponent)
            is ZeroGVector -> when {
                exponent == 0 -> this@GAlgebraContext.unit
                exponent > 0 -> this@GAlgebraContext.zeroGVector
                exponent < 0 -> throw ArithmeticException("Negative power in an algebra is not defined")
                else -> throw Exception("This can't happen!")
            }
        }
    }
}

open class GAlgebra<B : BasisName, D : Degree, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val matrixSpace: MatrixSpace<S, V, M>,
    degreeMonoid: DegreeMonoid<D>,
    name: String,
    getVectorSpace: (D) -> VectorSpace<B, S, V>,
    val getMultiplication: (D, D) -> BilinearMap<B, B, B, S, V, M>,
    unitVector: Vector<B, S, V>,
    printer: VectorPrinter<B, S, V> = DefaultVectorPrinter()
) : GVectorSpace<B, D, S, V>(matrixSpace.numVectorSpace, degreeMonoid, name, printer, getVectorSpace), GAlgebraOperations<B, D, S, V, M> {
    // use 'lazy' to avoid the following warning:
    //   Leaking 'this' in constructor of non-final class GAlgebra
    override val context by lazy {
        GAlgebraContext(matrixSpace.numVectorSpace.field, matrixSpace.numVectorSpace, this, this)
    }

    override val unit: GVector<B, D, S, V> = this.fromVector(unitVector, 0)

    private val multiplication: GBilinearMap<B, B, B, D, S, V, M> by lazy {
        val bilinearMapName = "Multiplication(${this.name})"
        GBilinearMap(this, this, this, 0, bilinearMapName) { p, q -> getMultiplication(p, q) }
    }
    override fun multiply(a: GVector<B, D, S, V>, b: GVector<B, D, S, V>): GVector<B, D, S, V> {
        return this.multiplication(a, b)
    }

    override fun multiply(a: GVectorOrZero<B, D, S, V>, b: GVectorOrZero<B, D, S, V>): GVectorOrZero<B, D, S, V> {
        return when (a) {
            is ZeroGVector -> this.zeroGVector
            is GVector -> when (b) {
                is ZeroGVector -> this.zeroGVector
                is GVector -> this.multiply(a, b)
            }
        }
    }

    fun isBasis(
        gVectorList: List<GVector<B, D, S, V>>,
        degree: D,
    ): Boolean {
        return this.isBasis(gVectorList, degree, this.matrixSpace)
    }

    fun isBasis(
        gVectorList: List<GVector<B, D, S, V>>,
        degree: Int,
    ): Boolean {
        return this.isBasis(gVectorList, degree, this.matrixSpace)
    }

    fun getId(): GAlgebraMap<B, B, D, S, V, M> {
        return GAlgebraMap(this, this, this.matrixSpace, "id") { degree ->
            this[degree].getId(this.matrixSpace)
        }
    }

    fun parse(generators: List<Pair<String, GVector<B, D, S, V>>>, text: String): GVectorOrZero<B, D, S, V> {
        val grammar = GAlgebraGrammar(this, generators)
        return grammar.parseToEnd(text)
    }
}

package com.github.shwaka.kohomology.dg

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.InternalPrintConfig
import com.github.shwaka.kohomology.vectsp.PrintConfig
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface GAlgebraOperations<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    public val unit: GVector<D, B, S, V>
}

public open class GAlgebraContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<D, B, S, V>,
    gMagmaOperations: GMagmaOperations<D, B, S, V, M>,
    gAlgebraOperations: GAlgebraOperations<D, B, S, V, M>,
) : GMagmaContext<D, B, S, V, M>(scalarOperations, numVectorOperations, gVectorOperations, gMagmaOperations),
    GAlgebraOperations<D, B, S, V, M> by gAlgebraOperations {
    public fun GVector<D, B, S, V>.pow(exponent: Int): GVector<D, B, S, V> {
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
    public fun GVectorOrZero<D, B, S, V>.pow(exponent: Int): GVectorOrZero<D, B, S, V> {
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
    public fun Iterable<GVector<D, B, S, V>>.product(): GVector<D, B, S, V> {
        return this.fold(this@GAlgebraContext.unit) { acc, x -> acc * x }
    }
}

public open class GAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    public val matrixSpace: MatrixSpace<S, V, M>,
    degreeGroup: DegreeGroup<D>,
    name: String,
    getVectorSpace: (D) -> VectorSpace<B, S, V>,
    public val getMultiplication: (D, D) -> BilinearMap<B, B, B, S, V, M>,
    unitVector: Vector<B, S, V>,
    getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<B, S>,
    listDegreesForAugmentedDegree: ((Int) -> List<D>)? = null,
) : GVectorSpace<D, B, S, V>(matrixSpace.numVectorSpace, degreeGroup, name, getInternalPrintConfig, listDegreesForAugmentedDegree, getVectorSpace),
    GMagmaOperations<D, B, S, V, M>,
    GAlgebraOperations<D, B, S, V, M> {
    public override val context: GAlgebraContext<D, B, S, V, M> by lazy {
        // use 'lazy' to avoid the following warning:
        //   Leaking 'this' in constructor of non-final class GAlgebra
        GAlgebraContext(matrixSpace.numVectorSpace.field, matrixSpace.numVectorSpace, this, this, this)
    }

    override val unit: GVector<D, B, S, V> by lazy {
        // use 'lazy' to avoid NullPointerException concerning
        // 'open val degreeGroup: DegreeGroup<D>' in GVectorSpace
        this.fromVector(unitVector, 0)
    }

    private val multiplication: GBilinearMap<B, B, B, D, S, V, M> by lazy {
        val bilinearMapName = "Multiplication(${this.name})"
        GBilinearMap(this, this, this, 0, bilinearMapName) { p, q -> getMultiplication(p, q) }
    }
    override fun multiply(a: GVector<D, B, S, V>, b: GVector<D, B, S, V>): GVector<D, B, S, V> {
        return this.multiplication(a, b)
    }

    override fun multiply(a: GVectorOrZero<D, B, S, V>, b: GVectorOrZero<D, B, S, V>): GVectorOrZero<D, B, S, V> {
        return when (a) {
            is ZeroGVector -> this.zeroGVector
            is GVector -> when (b) {
                is ZeroGVector -> this.zeroGVector
                is GVector -> this.multiply(a, b)
            }
        }
    }

    public fun isBasis(
        gVectorList: List<GVector<D, B, S, V>>,
        degree: D,
    ): Boolean {
        return this.isBasis(gVectorList, degree, this.matrixSpace)
    }

    public fun isBasis(
        gVectorList: List<GVector<D, B, S, V>>,
        degree: Int,
    ): Boolean {
        return this.isBasis(gVectorList, degree, this.matrixSpace)
    }

    public fun getId(): GAlgebraMap<D, B, B, S, V, M> {
        return GAlgebraMap(this, this, this.matrixSpace, "id") { degree ->
            this[degree].getId(this.matrixSpace)
        }
    }

    public fun parse(generators: List<Pair<String, GVector<D, B, S, V>>>, text: String): GVectorOrZero<D, B, S, V> {
        val grammar = GAlgebraGrammar(this, generators)
        return grammar.parseToEnd(text)
    }

    public fun getGLinearMapByMultiplication(cochain: GVector<D, B, S, V>): GLinearMap<D, B, B, S, V, M> {
        return GLinearMap.fromGVectors(
            this,
            this,
            cochain.degree,
            this.matrixSpace,
            "($cochain * (-))"
        ) { degree ->
            this.context.run {
                this@GAlgebra.getBasis(degree).map { cochain * it }
            }
        }
    }
}

package com.github.shwaka.kohomology.dg

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.DegreeGroup
import com.github.shwaka.kohomology.dg.parser.GAlgebraGrammar
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface GAlgebraContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GMagmaContext<D, B, S, V, M> {
    public val gAlgebra: GAlgebra<D, B, S, V, M>
    public val unit: GVector<D, B, S, V>
        get() = this.gAlgebra.unit

    public fun GVector<D, B, S, V>.pow(exponent: Int): GVector<D, B, S, V> {
        val unit = this@GAlgebraContext.gAlgebra.unit
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
                exponent == 0 -> this@GAlgebraContext.gAlgebra.unit
                exponent > 0 -> this@GAlgebraContext.gAlgebra.zeroGVector
                exponent < 0 -> throw ArithmeticException("Negative power in an algebra is not defined")
                else -> throw Exception("This can't happen!")
            }
        }
    }
    public fun Iterable<GVector<D, B, S, V>>.product(): GVector<D, B, S, V> {
        return this.fold(this@GAlgebraContext.gAlgebra.unit) { acc, x -> acc * x }
    }
}

public class GAlgebraContextImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val gAlgebra: GAlgebra<D, B, S, V, M>,
) : GAlgebraContext<D, B, S, V, M>,
    GMagmaContext<D, B, S, V, M> by GMagmaContextImpl(gAlgebra)

public interface GAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GMagma<D, B, S, V, M> {
    public val unit: GVector<D, B, S, V>
    public override val context: GAlgebraContext<D, B, S, V, M>
    public val underlyingGAlgebra: GAlgebra<D, B, S, V, M>

    override fun getIdentity(): GAlgebraMap<D, B, B, S, V, M> {
        return GAlgebraMap(this, this, this.matrixSpace, "id") { degree ->
            this[degree].getIdentity(this.matrixSpace)
        }
    }

    public fun parse(generators: List<Pair<String, GVector<D, B, S, V>>>, text: String): GVectorOrZero<D, B, S, V> {
        val grammar = GAlgebraGrammar(this, generators)
        return grammar.parseToEnd(text)
        // The following was commented out since it printed the message in tests.
        // try {
        //     return grammar.parseToEnd(text)
        // } catch (exception: ParseException) {
        //     val generatorsString = generators.joinToString(", ") { it.first }
        //     println("[Error] Failed to parse text.")
        //     println("  Expected generators are: $generatorsString")
        //     throw exception
        // }
    }

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            gVectorSpace: GVectorSpace<D, B, S, V>,
            multiplication: GBilinearMap<B, B, B, D, S, V, M>,
            unit: GVector<D, B, S, V>,
        ): GAlgebra<D, B, S, V, M> {
            return GAlgebraImpl(matrixSpace, gVectorSpace, multiplication, unit)
        }
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            degreeGroup: DegreeGroup<D>,
            name: String,
            getVectorSpace: (D) -> VectorSpace<B, S, V>,
            getMultiplication: (D, D) -> BilinearMap<B, B, B, S, V, M>,
            unitVector: Vector<B, S, V>,
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<B, S>,
            listDegreesForAugmentedDegree: ((Int) -> List<D>)? = null,
        ): GAlgebra<D, B, S, V, M> {
            val gVectorSpace = GVectorSpace(
                matrixSpace.numVectorSpace,
                degreeGroup,
                name,
                getInternalPrintConfig,
                listDegreesForAugmentedDegree,
                getVectorSpace
            )
            val bilinearMapName = "Multiplication($name)"
            val multiplication = GBilinearMap(gVectorSpace, gVectorSpace, gVectorSpace, 0, bilinearMapName) { p, q -> getMultiplication(p, q) }
            val unit = gVectorSpace.fromVector(unitVector, 0)
            return GAlgebraImpl(matrixSpace, gVectorSpace, multiplication, unit)
        }
    }
}

private class GAlgebraImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val matrixSpace: MatrixSpace<S, V, M>,
    gVectorSpace: GVectorSpace<D, B, S, V>,
    override val multiplication: GBilinearMap<B, B, B, D, S, V, M>,
    override val unit: GVector<D, B, S, V>,
) : GAlgebra<D, B, S, V, M>,
    GVectorSpace<D, B, S, V> by gVectorSpace {
    override val context: GAlgebraContext<D, B, S, V, M> = GAlgebraContextImpl(this)
    override val underlyingGAlgebra: GAlgebra<D, B, S, V, M> = this
}

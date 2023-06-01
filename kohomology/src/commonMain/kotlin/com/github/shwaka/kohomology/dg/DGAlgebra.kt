package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

public interface DGAlgebraContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    DGMagmaContext<D, B, S, V, M>, GAlgebraContext<D, B, S, V, M> {
    public val dgAlgebra: DGAlgebra<D, B, S, V, M>
}

internal class DGAlgebraContextImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val dgAlgebra: DGAlgebra<D, B, S, V, M>,
) : DGAlgebraContext<D, B, S, V, M>,
    DGMagmaContext<D, B, S, V, M> by DGMagmaContextImpl(dgAlgebra) {
    override val gAlgebra: GAlgebra<D, B, S, V, M> = dgAlgebra

    // public fun GVector<D, B, S, V>.pow(exponent: Int): GVector<D, B, S, V> {
    //     return this@DGAlgebraContextImpl.gAlgebraContext.run { this@pow.pow(exponent) }
    // }
}

public interface DGAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    DGMagma<D, B, S, V, M>, GAlgebra<D, B, S, V, M> {
    override val context: DGAlgebraContext<D, B, S, V, M>
    override val differential: Derivation<D, B, S, V, M>
    override val cohomology: SubQuotGAlgebra<D, B, S, V, M>

    override fun getIdentity(): DGAlgebraMap<D, B, B, S, V, M> {
        val gAlgebraMap = this.underlyingGAlgebra.getIdentity()
        return DGAlgebraMap(this, this, gAlgebraMap)
    }

    override fun getQuotientByIdeal(ideal: SubGVectorSpace<D, B, S, V, M>): SubQuotDGAlgebra<D, B, S, V, M> {
        val subQuotGAlgebra = super.getQuotientByIdeal(ideal)
        return SubQuotDGAlgebra(
            subQuotGAlgebra,
            this.differential.induce(subQuotGAlgebra),
        )
    }

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            gAlgebra: GAlgebra<D, B, S, V, M>,
            differential: Derivation<D, B, S, V, M>,
        ): DGAlgebra<D, B, S, V, M> {
            val dgMagma = DGMagma(gAlgebra, differential)
            return DGAlgebraImpl(
                gAlgebra,
                differential,
                dgMagma.cohomology,
                dgMagma.cohomology.multiplication,
            )
        }
    }
}

private class DGAlgebraImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val underlyingGAlgebra: GAlgebra<D, B, S, V, M>,
    override val differential: Derivation<D, B, S, V, M>,
    private val cohomologyGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M>,
    private val cohomologyMultiplication: GBilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, D, S, V, M>,
) : DGAlgebra<D, B, S, V, M>,
    GVectorSpace<D, B, S, V> by underlyingGAlgebra {
    override val context: DGAlgebraContext<D, B, S, V, M> = DGAlgebraContextImpl(this)
    override val unit: GVector<D, B, S, V> = underlyingGAlgebra.unit
    override val isCommutative: Boolean = underlyingGAlgebra.isCommutative
    override val matrixSpace: MatrixSpace<S, V, M> = underlyingGAlgebra.matrixSpace
    override val multiplication: GBilinearMap<B, B, B, D, S, V, M> = underlyingGAlgebra.multiplication

    override val cohomology: SubQuotGAlgebra<D, B, S, V, M> by lazy {
        val cohomologyUnit = DGVectorSpace.getCohomologyClass(
            this.cohomologyGVectorSpace,
            this.underlyingGAlgebra.unit,
        )
        SubQuotGAlgebra(
            this.matrixSpace,
            this.cohomologyGVectorSpace,
            this.cohomologyMultiplication,
            cohomologyUnit,
            this.isCommutative, // inherit commutativity from the underlying dg algebra
        )
    }
}

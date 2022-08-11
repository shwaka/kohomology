package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace

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
    override fun getIdentity(): DGAlgebraMap<D, B, B, S, V, M>
    override val cohomology: SubQuotGAlgebra<D, B, S, V, M>
}

internal open class DGAlgebraImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    open val gAlgebra: GAlgebra<D, B, S, V, M>,
    override val differential: Derivation<D, B, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>
) : DGMagma<D, B, S, V, M> by DGMagma(gAlgebra, differential),
    DGAlgebra<D, B, S, V, M> {
    override val context: DGAlgebraContext<D, B, S, V, M> by lazy {
        DGAlgebraContextImpl(this)
    }
    override val unit: GVector<D, B, S, V> by lazy { gAlgebra.unit }

    override val cohomology: SubQuotGAlgebra<D, B, S, V, M> by lazy {
        val cohomOfDeg0: SubQuotVectorSpace<B, S, V, M> = this.cohomology[0]
        val cohomologyUnit = cohomOfDeg0.projection(this.gAlgebra.unit.vector)
        val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<SubQuotBasis<B, S, V>, S> = { printConfig: PrintConfig ->
            SubQuotVectorSpace.convertInternalPrintConfig(printConfig, this.gAlgebra.getInternalPrintConfig(printConfig))
        }
        SubQuotGAlgebra(
            matrixSpace,
            this.degreeGroup,
            this.cohomologyName,
            this.cohomology::get,
            this.cohomology.multiplication,
            cohomologyUnit,
            listDegreesForAugmentedDegree = this.gAlgebra.listDegreesForAugmentedDegree,
            getInternalPrintConfig = getInternalPrintConfig
        )
    }

    override fun getIdentity(): DGAlgebraMap<D, B, B, S, V, M> {
        val gAlgebraMap = this.gAlgebra.getIdentity()
        return DGAlgebraMap(this, this, gAlgebraMap)
    }
}

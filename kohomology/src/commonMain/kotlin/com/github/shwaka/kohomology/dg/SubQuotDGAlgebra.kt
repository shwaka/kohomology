package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

public interface SubQuotDGAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    DGAlgebra<D, SubQuotBasis<B, S, V>, S, V, M>,
    SubQuotGAlgebra<D, B, S, V, M> {
    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            gAlgebra: SubQuotGAlgebra<D, B, S, V, M>,
            differential: Derivation<D, SubQuotBasis<B, S, V>, S, V, M>,
        ): SubQuotDGAlgebra<D, B, S, V, M> {
            val dgMagma = DGMagma(gAlgebra, differential)
            return SubQuotDGAlgebraImpl(
                gAlgebra,
                differential,
                dgMagma.cohomology,
                dgMagma.cohomology.multiplication,
            )
        }
    }
}

private class SubQuotDGAlgebraImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val underlyingGAlgebra: SubQuotGAlgebra<D, B, S, V, M>,
    override val differential: Derivation<D, SubQuotBasis<B, S, V>, S, V, M>,
    private val cohomologyGVectorSpace: SubQuotGVectorSpace<D, SubQuotBasis<B, S, V>, S, V, M>,
    private val cohomologyMultiplication: GBilinearMap<
        SubQuotBasis<SubQuotBasis<B, S, V>, S, V>,
        SubQuotBasis<SubQuotBasis<B, S, V>, S, V>,
        SubQuotBasis<SubQuotBasis<B, S, V>, S, V>,
        D, S, V, M>,
) : SubQuotDGAlgebra<D, B, S, V, M>,
    SubQuotGVectorSpace<D, B, S, V, M> by underlyingGAlgebra {

    override val context: DGAlgebraContext<D, SubQuotBasis<B, S, V>, S, V, M> = DGAlgebraContextImpl(this)
    override val unit: GVector<D, SubQuotBasis<B, S, V>, S, V> = underlyingGAlgebra.unit
    override val isCommutative: Boolean = underlyingGAlgebra.isCommutative
    override val matrixSpace: MatrixSpace<S, V, M> = underlyingGAlgebra.matrixSpace
    override val multiplication: GBilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, D, S, V, M> =
        underlyingGAlgebra.multiplication

    override val cohomology: SubQuotGAlgebra<D, SubQuotBasis<B, S, V>, S, V, M> by lazy {
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

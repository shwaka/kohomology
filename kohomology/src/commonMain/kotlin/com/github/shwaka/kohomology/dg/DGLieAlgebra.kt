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

public interface DGLieAlgebraContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    DGMagmaContext<D, B, S, V, M>, GLieAlgebraContext<D, B, S, V, M> {
    public val dgLieAlgebra: DGLieAlgebra<D, B, S, V, M>
}

internal class DGLieAlgebraContextImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val dgLieAlgebra: DGLieAlgebra<D, B, S, V, M>,
) : DGLieAlgebraContext<D, B, S, V, M>,
    DGMagmaContext<D, B, S, V, M> by DGMagmaContextImpl(dgLieAlgebra) {
    override val gLieAlgebra: GLieAlgebra<D, B, S, V, M> = dgLieAlgebra
}

public interface DGLieAlgebra<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    DGMagma<D, B, S, V, M>, GLieAlgebra<D, B, S, V, M> {
    override val context: DGLieAlgebraContext<D, B, S, V, M>
    override val cohomology: SubQuotGLieAlgebra<D, B, S, V, M>

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            gLieAlgebra: GLieAlgebra<D, B, S, V, M>,
            differential: GLinearMap<D, B, B, S, V, M>,
        ): DGLieAlgebra<D, B, S, V, M> {
            val dgMagma = DGMagma(gLieAlgebra, differential)
            return DGLieAlgebraImpl(
                gLieAlgebra,
                differential,
                dgMagma.cohomology,
                dgMagma.cohomology.multiplication,
            )
        }
    }
}

private class DGLieAlgebraImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    gLieAlgebra: GLieAlgebra<D, B, S, V, M>,
    differential: GLinearMap<D, B, B, S, V, M>,
    private val cohomologyGVectorSpace: SubQuotGVectorSpace<D, B, S, V, M>,
    private val cohomologyMultiplication: GBilinearMap<SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, SubQuotBasis<B, S, V>, D, S, V, M>,
) : DGMagma<D, B, S, V, M> by DGMagma(gLieAlgebra, differential),
    DGLieAlgebra<D, B, S, V, M> {
    override val context: DGLieAlgebraContext<D, B, S, V, M> by lazy {
        DGLieAlgebraContextImpl(this)
    }

    override val cohomology: SubQuotGLieAlgebra<D, B, S, V, M> by lazy {
        SubQuotGLieAlgebra(
            matrixSpace,
            this.cohomologyGVectorSpace,
            this.cohomologyMultiplication,
        )
    }
}

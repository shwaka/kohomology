package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface SelfModule<
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : Module<B, B, S, V, M> {

    public companion object {
        public operator fun <B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            coeffAlgebra: Algebra<B, S, V, M>,
        ): SelfModule<B, S, V, M> {
            return SelfModuleImpl(coeffAlgebra)
        }
    }
}

private class SelfModuleImpl<
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(override val coeffAlgebra: Algebra<B, S, V, M>) : SelfModule<B, S, V, M> {

    override val context: ModuleContext<B, B, S, V, M> = ModuleContextImpl(this)
    override val matrixSpace: MatrixSpace<S, V, M> = coeffAlgebra.matrixSpace
    override val underlyingVectorSpace: VectorSpace<B, S, V> = coeffAlgebra
    override val action: BilinearMap<B, B, B, S, V, M> = coeffAlgebra.multiplication
}

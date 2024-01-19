package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

public object SelfModuleBasisName : BasisName {
    override fun toString(): String {
        return "1"
    }
}

public interface SelfModule<
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : FreeModule<B, SelfModuleBasisName, S, V, M> {

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
    >(
    override val coeffAlgebra: Algebra<B, S, V, M>
) : SelfModule<B, S, V, M>,
    FreeModule<B, SelfModuleBasisName, S, V, M> by FreeModule(coeffAlgebra, listOf(SelfModuleBasisName))

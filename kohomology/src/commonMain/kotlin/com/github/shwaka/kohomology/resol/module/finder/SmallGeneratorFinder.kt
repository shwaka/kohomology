package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.Algebra
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.Vector

public interface SmallGeneratorFinder<
    BA : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    in Alg : Algebra<BA, S, V, M>,
    > {

    public fun <B : BasisName> find(
        module: Module<BA, B, S, V, M>,
    ): List<Vector<B, S, V>>

    public companion object {
        public fun <
            BA : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            Alg : Algebra<BA, S, V, M>,
            > getDefaultFor(coeffAlgebra: Alg): SmallGeneratorFinder<BA, S, V, M, Alg> {
            return EarlyReturnSelector(coeffAlgebra)
        }
    }
}

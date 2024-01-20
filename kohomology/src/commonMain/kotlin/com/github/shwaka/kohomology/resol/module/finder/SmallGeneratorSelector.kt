package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.Algebra
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.Vector

public interface SmallGeneratorSelector<
    BA : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    Alg : Algebra<BA, S, V, M>,
    > : SmallGeneratorFinder<BA, S, V, M, Alg> {

    public fun <BA : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> select(
        module: Module<BA, B, S, V, M>,
        generator: List<Vector<B, S, V>>,
    ): List<Vector<B, S, V>>

    override fun <B : BasisName> find(
        module: Module<BA, B, S, V, M>,
    ): List<Vector<B, S, V>> {
        return this.select(module, module.underlyingVectorSpace.getBasis())
    }
}

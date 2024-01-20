package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.module.Module
import com.github.shwaka.kohomology.resol.module.MonoidRing
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.Vector

public class MonoidRingFinder<
    E : FiniteMonoidElement,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val coeffAlgebra: MonoidRing<E, S, V, M>,
) : SmallGeneratorFinder<E, S, V, M, MonoidRing<E, S, V, M>> {
    private val finder: SmallGeneratorFinder<E, S, V, M, MonoidRing<E, S, V, M>> =
        SimpleSelector(coeffAlgebra)

    override fun <B : BasisName> find(module: Module<E, B, S, V, M>): List<Vector<B, S, V>> {
        return this.finder.find(module)
    }
}

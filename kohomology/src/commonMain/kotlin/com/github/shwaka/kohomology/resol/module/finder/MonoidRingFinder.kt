package com.github.shwaka.kohomology.resol.module.finder

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.Augmentation
import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement

public class MonoidRingFinder<
    E : FiniteMonoidElement,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    private val coeffAlgebra: MonoidRing<E, S, V, M>,
) : SmallGeneratorFinder<E, S, V, M, MonoidRing<E, S, V, M>> by QuotFinder(
    coeffAlgebraMap = Augmentation(coeffAlgebra),
    sourceCoeffAlgebra = coeffAlgebra,
    finderOnQuot = TrivialSelector(),
    requireAdditionalGenerator = false,
)

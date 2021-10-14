package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGLieAlgebra
import com.github.shwaka.kohomology.dg.GLinearMap
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

private class DerivationDGLieAlgebraFactory<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
) {
    val matrixSpace = freeDGAlgebra.matrixSpace
    val degreeGroup = freeDGAlgebra.gAlgebra.degreeGroup
    val gLieAlgebra = DerivationGLieAlgebra(freeDGAlgebra.gAlgebra)
    val differential: GLinearMap<D, DerivationBasis<D, I>, DerivationBasis<D, I>, S, V, M> = TODO()
}

public class DerivationDGLieAlgebra<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: DerivationDGLieAlgebraFactory<D, I, S, V, M>,
) : DGLieAlgebra<D, DerivationBasis<D, I>, S, V, M>(factory.gLieAlgebra, factory.differential, factory.matrixSpace) {
    public val freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M> = factory.freeDGAlgebra

    public companion object {
        public operator fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>
        ): DerivationDGLieAlgebra<D, I, S, V, M> {
            val factory = DerivationDGLieAlgebraFactory(freeDGAlgebra)
            return DerivationDGLieAlgebra(factory)
        }
    }
}

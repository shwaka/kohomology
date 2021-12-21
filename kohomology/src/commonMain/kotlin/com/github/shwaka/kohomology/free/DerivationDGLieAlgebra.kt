package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGDerivation
import com.github.shwaka.kohomology.dg.DGLieAlgebra
import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.LieDerivation
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

private class DerivationDGLieAlgebraFactory<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
) {
    val matrixSpace = freeDGAlgebra.matrixSpace
    val degreeGroup = freeDGAlgebra.degreeGroup
    val gLieAlgebra = DerivationGLieAlgebra(freeDGAlgebra.gAlgebra)
    val differential: LieDerivation<D, DerivationBasis<D, I>, S, V, M> = gLieAlgebra.context.run {
        val differentialAsGVector = gLieAlgebra.derivationToGVector(freeDGAlgebra.differential)
        ad(differentialAsGVector)
    }
}

public class DerivationDGLieAlgebra<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: DerivationDGLieAlgebraFactory<D, I, S, V, M>,
) : DGLieAlgebra<D, DerivationBasis<D, I>, S, V, M>(factory.gLieAlgebra, factory.differential, factory.matrixSpace) {
    public val freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M> = factory.freeDGAlgebra
    override val gLieAlgebra: DerivationGLieAlgebra<D, I, S, V, M> = factory.gLieAlgebra

    public fun gVectorToDerivation(gVector: GVector<D, DerivationBasis<D, I>, S, V>): Derivation<D, Monomial<D, I>, S, V, M> {
        return this.gLieAlgebra.gVectorToDerivation(gVector)
    }

    public fun gVectorToDGDerivation(gVector: GVector<D, DerivationBasis<D, I>, S, V>): DGDerivation<D, Monomial<D, I>, S, V, M> {
        this.context.run {
            if (d(gVector).isNotZero()) {
                throw IllegalArgumentException("gVector must be a cocycle, but d($gVector) = ${d(gVector)}")
            }
        }
        return DGDerivation(this.freeDGAlgebra, this.gVectorToDerivation(gVector))
    }

    public fun derivationToGVector(derivation: Derivation<D, Monomial<D, I>, S, V, M>): GVector<D, DerivationBasis<D, I>, S, V> {
        return this.gLieAlgebra.derivationToGVector(derivation)
    }

    public companion object {
        public operator fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>
        ): DerivationDGLieAlgebra<D, I, S, V, M> {
            val factory = DerivationDGLieAlgebraFactory(freeDGAlgebra)
            return DerivationDGLieAlgebra(factory)
        }
    }
}

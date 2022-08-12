package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGDerivation
import com.github.shwaka.kohomology.dg.DGLieAlgebra
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.LieDerivation
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.Printable

private class DerivationDGLieAlgebraFactory<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M>,
) {
    val matrixSpace = freeDGAlgebra.matrixSpace
    val degreeGroup = freeDGAlgebra.degreeGroup
    val gLieAlgebra = DerivationGLieAlgebra(freeDGAlgebra)
    val differential: LieDerivation<D, DerivationBasis<D, I>, S, V, M> = gLieAlgebra.context.run {
        val differentialAsGVector = this@DerivationDGLieAlgebraFactory.gLieAlgebra.derivationToGVector(freeDGAlgebra.differential)
        ad(differentialAsGVector)
    }
}

public class DerivationDGLieAlgebra<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: DerivationDGLieAlgebraFactory<D, I, S, V, M>,
) : DGLieAlgebra<D, DerivationBasis<D, I>, S, V, M> by DGLieAlgebra(factory.gLieAlgebra, factory.differential),
    Printable {
    public val freeDGAlgebra: FreeDGAlgebra<D, I, S, V, M> = factory.freeDGAlgebra
    public val gLieAlgebra: DerivationGLieAlgebra<D, I, S, V, M> = factory.gLieAlgebra

    public fun gVectorToDGDerivation(gVector: GVector<D, DerivationBasis<D, I>, S, V>): DGDerivation<D, Monomial<D, I>, S, V, M> {
        this.context.run {
            if (d(gVector).isNotZero()) {
                throw IllegalArgumentException("gVector must be a cocycle, but d($gVector) = ${d(gVector)}")
            }
        }
        val derivation = this.gLieAlgebra.gVectorToDerivation(gVector)
        return DGDerivation(this.freeDGAlgebra, derivation)
    }

    public fun dgDerivationToGVector(dgDerivation: DGDerivation<D, Monomial<D, I>, S, V, M>): GVector<D, DerivationBasis<D, I>, S, V> {
        // dgDerivation is assumed to commute with d
        return this.gLieAlgebra.derivationToGVector(dgDerivation)
    }

    override fun toString(printConfig: PrintConfig): String {
        val gLieAlgebraString = this.gLieAlgebra.toString(printConfig)
        return "($gLieAlgebraString, d)"
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

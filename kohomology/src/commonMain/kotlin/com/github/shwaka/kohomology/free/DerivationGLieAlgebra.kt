package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.Derivation
import com.github.shwaka.kohomology.dg.GLieAlgebra
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.Monomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.DirectSum
import com.github.shwaka.kohomology.vectsp.DirectSumBasis
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

public typealias DerivationBasis<D, I> = DirectSumBasis<Monomial<D, I>>

private class DerivationGLieAlgebraFactory<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val freeGAlgebra: FreeGAlgebra<D, I, S, V, M>,
) {
    val matrixSpace = freeGAlgebra.matrixSpace
    val degreeGroup = freeGAlgebra.degreeGroup
    val name = "Der(${freeGAlgebra.name})"

    private val cache: MutableMap<D, DirectSum<Monomial<D, I>, S, V, M>> = mutableMapOf()

    fun getVectorSpace(derivationDegree: D): DirectSum<Monomial<D, I>, S, V, M> {
        this.cache[derivationDegree]?.let {
            // if cache exists
            return it
        }
        // if cache does not exist
        val generatorDegreeList = this.freeGAlgebra.indeterminateList.map { it.degree }
        val vectorSpaceList: List<VectorSpace<Monomial<D, I>, S, V>> = this.degreeGroup.context.run {
            generatorDegreeList.map { degree -> freeGAlgebra[degree + derivationDegree] }
        }
        return DirectSum(vectorSpaceList, freeGAlgebra.matrixSpace)
    }

    fun getMultiplication(derivationDegree1: D, derivationDegree2: D): BilinearMap<DerivationBasis<D, I>, DerivationBasis<D, I>, DerivationBasis<D, I>, S, V, M> {
        val source1 = this.getVectorSpace(derivationDegree1)
        val source2 = this.getVectorSpace(derivationDegree2)
        val target = this.getVectorSpace(
            this.degreeGroup.context.run { derivationDegree1 + derivationDegree2 }
        )
        return ValueBilinearMap(source1, source2, target, this.matrixSpace, this.generateGetValue(target))
    }

    private fun generateGetValue(target: VectorSpace<DerivationBasis<D, I>, S, V>): (DerivationBasis<D, I>, DerivationBasis<D, I>) -> Vector<DerivationBasis<D, I>, S, V> {
        return { derivationBasis1, derivationBasis2 ->
            val derivation1 = derivationBasis1.toDerivation()
            val derivation2 = derivationBasis2.toDerivation()
            val sign = derivation1.degree.koszulSign(derivation2.degree)
            val valueList = this.freeGAlgebra.context.run {
                this@DerivationGLieAlgebraFactory.freeGAlgebra.generatorList.map { gVector ->
                    derivation1(derivation2(gVector)) - sign * derivation2(derivation1(gVector))
                }
            }.map { gVector -> gVector.vector }
            TODO()
        }
    }

    private fun DerivationBasis<D, I>.toDerivation(): Derivation<D, Monomial<D, I>, S, V, M> {
        TODO()
    }
}

public class DerivationGLieAlgebra<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: DerivationGLieAlgebraFactory<D, I, S, V, M>
) : GLieAlgebra<D, DerivationBasis<D, I>, S, V, M>(factory.matrixSpace, factory.degreeGroup, factory.name, factory::getVectorSpace, factory::getMultiplication) {
    public val freeGAlgebra: FreeGAlgebra<D, I, S, V, M> = factory.freeGAlgebra
}

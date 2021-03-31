package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.GAlgebra
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.Degree
import com.github.shwaka.kohomology.vectsp.GVector
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

private class FreeGAlgebraFactory<I, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val matrixSpace: MatrixSpace<S, V, M>,
    val generatorList: List<Indeterminate<I>>,
) {
    private fun getBasisNames(degree: Degree): List<Monomial<I>> {
        return Monomial.listAll(this.generatorList, degree)
    }

    fun getVectorSpace(degree: Degree): VectorSpace<Monomial<I>, S, V> {
        return VectorSpace(this.matrixSpace.numVectorSpace, this.getBasisNames(degree))
    }

    fun getMultiplication(p: Degree, q: Degree): BilinearMap<Monomial<I>, Monomial<I>, Monomial<I>, S, V, M> {
        val source1 = this.getVectorSpace(p)
        val source2 = this.getVectorSpace(q)
        val target = this.getVectorSpace(p + q)
        val values = source1.basisNames.map { monomial1 ->
            source2.basisNames.map { monomial2 ->
                (monomial1 * monomial2)?.let {
                    val (monomial: Monomial<I>, sign: Int) = it
                    target.fromBasisName(monomial, sign)
                } ?: target.zeroVector
            }
        }
        return BilinearMap.fromVectors(source1, source2, target, this.matrixSpace, values)
    }

    val unitVector: Vector<Monomial<I>, S, V> = this.getVectorSpace(0).getBasis()[0]
}

class FreeGAlgebra<I, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    val matrixSpace: MatrixSpace<S, V, M>,
    factory: FreeGAlgebraFactory<I, S, V, M>
) : GAlgebra<Monomial<I>, S, V, M>(matrixSpace, factory::getVectorSpace, factory::getMultiplication, factory.unitVector) {
    val indeterminateList: List<Indeterminate<I>> = factory.generatorList
    val generatorList: List<GVector<Monomial<I>, S, V>>
        get() = this.indeterminateList.map { indeterminate ->
            val monomial = Monomial.fromIndeterminate(this.indeterminateList, indeterminate)
            this.fromBasisName(monomial, indeterminate.degree)
        }

    companion object {
        operator fun <I, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            generatorList: List<Indeterminate<I>>,
        ): FreeGAlgebra<I, S, V, M> {
            val factory = FreeGAlgebraFactory(matrixSpace, generatorList)
            return FreeGAlgebra(matrixSpace, factory)
        }
    }
}

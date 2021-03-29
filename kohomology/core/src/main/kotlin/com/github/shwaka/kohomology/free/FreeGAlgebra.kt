package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.GAlgebra
import com.github.shwaka.kohomology.dg.GBilinearMap
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.Degree

private class FreeGAlgebraFactory<I, S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>>(
    private val matrixSpace: MatrixSpace<S, V, M>,
    val generatorList: List<Indeterminate<I>>,
) {
    fun getBasisNames(degree: Degree): List<Monomial<I>> {
        return Monomial.listAll(this.generatorList, degree)
    }

    val multiplication: GBilinearMap<Monomial<I>, Monomial<I>, Monomial<I>, S, V, M> by lazy {
        GBilinearMap()
    }
}

class FreeGAlgebra<I, S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> private constructor(
    val matrixSpace: MatrixSpace<S, V, M>,
    factory: FreeGAlgebraFactory<I, S, V, M>
) : GAlgebra<Monomial<I>, S, V, M>(matrixSpace, factory::getBasisNames, factory.multiplication) {
    val generatorList: List<Indeterminate<I>> = factory.generatorList

    companion object {
        operator fun <I, S : Scalar<S>, V : NumVector<S, V>, M : Matrix<S, V, M>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            generatorList: List<Indeterminate<I>>,
        ): FreeGAlgebra<I, S, V, M> {
            val factory = FreeGAlgebraFactory(matrixSpace, generatorList)
            return FreeGAlgebra(matrixSpace, factory)
        }
    }
}

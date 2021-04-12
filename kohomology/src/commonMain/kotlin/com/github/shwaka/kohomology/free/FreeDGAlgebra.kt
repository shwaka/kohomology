package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.GAlgebraContext
import com.github.shwaka.kohomology.dg.GLinearMap
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.GVectorOrZero
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar

open class FreeDGAlgebra<I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> (
    override val gAlgebra: FreeGAlgebra<I, S, V, M>,
    differential: GLinearMap<Monomial<I>, Monomial<I>, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>
) : DGAlgebra<Monomial<I>, S, V, M>(gAlgebra, differential, matrixSpace) {
    companion object {
        operator fun <I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            indeterminateList: List<Indeterminate<I>>,
            getDifferentialValueList: GAlgebraContext<Monomial<I>, S, V, M>.(List<GVector<Monomial<I>, S, V>>) -> List<GVectorOrZero<Monomial<I>, S, V>>
        ): FreeDGAlgebra<I, S, V, M> {
            val freeGAlgebra: FreeGAlgebra<I, S, V, M> = FreeGAlgebra(matrixSpace, indeterminateList)
            val differential: GLinearMap<Monomial<I>, Monomial<I>, S, V, M> = freeGAlgebra.getDerivation(
                valueList = freeGAlgebra.context.run { getDifferentialValueList(freeGAlgebra.generatorList) },
                derivationDegree = 1
            )
            return FreeDGAlgebra(freeGAlgebra, differential, matrixSpace)
        }
    }
}

package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.DGAlgebra
import com.github.shwaka.kohomology.dg.GAlgebraContext
import com.github.shwaka.kohomology.dg.GLinearMap
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.GVector
import com.github.shwaka.kohomology.vectsp.GVectorOrZero

private class FreeDGAlgebraFactory<I, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val matrixSpace: MatrixSpace<S, V, M>,
    val indeterminateList: List<Indeterminate<I>>,
    val getDifferentialValueList: GAlgebraContext<Monomial<I>, S, V, M>.(List<GVector<Monomial<I>, S, V>>) -> List<GVectorOrZero<Monomial<I>, S, V>>
) {
    val freeGAlgebra: FreeGAlgebra<I, S, V, M> = FreeGAlgebra(this.matrixSpace, this.indeterminateList)
    val differential: GLinearMap<Monomial<I>, Monomial<I>, S, V, M> = freeGAlgebra.getDerivation(
        valueList = freeGAlgebra.withGAlgebraContext { getDifferentialValueList(freeGAlgebra.generatorList) },
        derivationDegree = 1
    )
}

class FreeDGAlgebra<I, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    factory: FreeDGAlgebraFactory<I, S, V, M>
) : DGAlgebra<Monomial<I>, S, V, M>(factory.freeGAlgebra, factory.differential) {
    override val gAlgebra: FreeGAlgebra<I, S, V, M> = factory.freeGAlgebra

    companion object {
        operator fun <I, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            indeterminateList: List<Indeterminate<I>>,
            getDifferentialValueList: GAlgebraContext<Monomial<I>, S, V, M>.(List<GVector<Monomial<I>, S, V>>) -> List<GVectorOrZero<Monomial<I>, S, V>>
        ): FreeDGAlgebra<I, S, V, M> {
            val factory = FreeDGAlgebraFactory(matrixSpace, indeterminateList, getDifferentialValueList)
            return FreeDGAlgebra(factory)
        }
    }
}

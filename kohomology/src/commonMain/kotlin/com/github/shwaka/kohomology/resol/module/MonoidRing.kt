package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoid
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace

private fun <E : FiniteMonoidElement, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> createMonoidRing(
    monoid: FiniteMonoid<E>,
    matrixSpace: MatrixSpace<S, V, M>,
): Algebra<E, S, V, M> {
    val vectorSpace = VectorSpace(matrixSpace.numVectorSpace, monoid.elements)
    val unit = vectorSpace.fromBasisName(monoid.unit)
    val isCommutative = monoid.isCommutative
    val values = monoid.multiplicationTable.map { row: List<E> ->
        row.map { monoidElement ->
            vectorSpace.fromBasisName(monoidElement)
        }
    }
    val multiplication = ValueBilinearMap(
        source1 = vectorSpace,
        source2 = vectorSpace,
        target = vectorSpace,
        matrixSpace = matrixSpace,
        values = values,
    )
    return Algebra(matrixSpace, vectorSpace, multiplication, unit, isCommutative)
}

public class MonoidRing<E : FiniteMonoidElement, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    public val monoid: FiniteMonoid<E>,
    matrixSpace: MatrixSpace<S, V, M>,
) : Algebra<E, S, V, M> by createMonoidRing(monoid, matrixSpace) {
    public fun <B : BasisName> getModuleWithTrivialAction(
        vectorSpace: VectorSpace<B, S, V>
    ): Module<E, B, S, V, M> {
        val action = ValueBilinearMap(
            source1 = this,
            source2 = vectorSpace,
            target = vectorSpace,
            matrixSpace = this.matrixSpace,
            values = List(this.dim) { vectorSpace.getBasis() },
        )
        return Module(
            matrixSpace = this.matrixSpace,
            underlyingVectorSpace = vectorSpace,
            coeffAlgebra = this,
            action = action,
        )
    }
}

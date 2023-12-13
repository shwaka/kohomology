package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorSpace
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoid
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.Vector
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface MonoidRing<
    E : FiniteMonoidElement,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : Algebra<E, S, V, M> {

    public val monoid: FiniteMonoid<E>

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

    public companion object {
        public operator fun <E : FiniteMonoidElement, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            monoid: FiniteMonoid<E>,
            matrixSpace: MatrixSpace<S, V, M>,
        ): MonoidRing<E, S, V, M> {
            return MonoidRingImpl(monoid, matrixSpace)
        }
    }
}

private class MonoidRingImpl<E : FiniteMonoidElement, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val monoid: FiniteMonoid<E>,
    override val matrixSpace: MatrixSpace<S, V, M>,
) : MonoidRing<E, S, V, M> {
    override val basisNames: List<E> = monoid.elements
    override val getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<E, S>
        get() = { InternalPrintConfig.default(it) }
    override val numVectorSpace: NumVectorSpace<S, V> = matrixSpace.numVectorSpace
    override fun indexOf(basisName: E): Int {
        return this.basisNames.indexOf(basisName)
    }

    override val context: AlgebraContext<E, S, V, M> = AlgebraContextImpl(this)
    override val unit: Vector<E, S, V> = this.fromBasisName(monoid.unit)
    override val isCommutative: Boolean = monoid.isCommutative
    override val multiplication: BilinearMap<E, E, E, S, V, M> = run {
        val values = monoid.multiplicationTable.map { row: List<E> ->
            row.map { monoidElement ->
                this.fromBasisName(monoidElement)
            }
        }
        ValueBilinearMap(
            source1 = this,
            source2 = this,
            target = this,
            matrixSpace = matrixSpace,
            values = values,
        )
    }
}

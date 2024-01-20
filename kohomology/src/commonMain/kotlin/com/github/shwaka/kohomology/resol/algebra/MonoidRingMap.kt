package com.github.shwaka.kohomology.resol.algebra

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidMap
import com.github.shwaka.kohomology.vectsp.LinearMap

public interface MonoidRingMap<
    ES : FiniteMonoidElement,
    ET : FiniteMonoidElement,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : AlgebraMap<ES, ET, S, V, M> {

    override val source: MonoidRing<ES, S, V, M>
    override val target: MonoidRing<ET, S, V, M>

    public val monoidMap: FiniteMonoidMap<ES, ET>

    public companion object {
        public operator fun <
            ES : FiniteMonoidElement,
            ET : FiniteMonoidElement,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            monoidMap: FiniteMonoidMap<ES, ET>,
            matrixSpace: MatrixSpace<S, V, M>,
            source: MonoidRing<ES, S, V, M> = MonoidRing(monoidMap.source, matrixSpace),
            target: MonoidRing<ET, S, V, M> = MonoidRing(monoidMap.target, matrixSpace),
        ): MonoidRingMap<ES, ET, S, V, M> {
            return MonoidRingMapImpl(monoidMap, matrixSpace, source, target)
        }
    }
}

private class MonoidRingMapImpl<
    ES : FiniteMonoidElement,
    ET : FiniteMonoidElement,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val monoidMap: FiniteMonoidMap<ES, ET>,
    matrixSpace: MatrixSpace<S, V, M>,
    override val source: MonoidRing<ES, S, V, M>,
    override val target: MonoidRing<ET, S, V, M>,
) : MonoidRingMap<ES, ET, S, V, M> {
    override val underlyingLinearMap: LinearMap<ES, ET, S, V, M> by lazy {
        val vectors = this.source.basisNames.map { monoidElement ->
            this.target.fromBasisName(this.monoidMap(monoidElement))
        }
        LinearMap.fromVectors(this.source, this.target, matrixSpace, vectors)
    }
}

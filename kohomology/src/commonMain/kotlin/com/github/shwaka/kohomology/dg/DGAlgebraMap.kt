package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap

public interface DGAlgebraMap<D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    DGLinearMap<D, BS, BT, S, V, M>, GAlgebraMap<D, BS, BT, S, V, M> {
    override val source: DGAlgebra<D, BS, S, V, M>
    override val target: DGAlgebra<D, BT, S, V, M>

    public operator fun <BR : BasisName> times(other: DGAlgebraMap<D, BR, BS, S, V, M>): DGAlgebraMap<D, BR, BT, S, V, M> {
        require(other.target == this.source) {
            "Cannot composite dg linear maps since the source of $this and the target of $other are different"
        }
        return DGAlgebraMap(
            source = other.source,
            target = this.target,
            gLinearMap = this * other,
        )
    }

    public companion object {
        public operator fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: DGAlgebra<D, BS, S, V, M>,
            target: DGAlgebra<D, BT, S, V, M>,
            gLinearMap: GAlgebraMap<D, BS, BT, S, V, M>,
        ): DGAlgebraMap<D, BS, BT, S, V, M> {
            return DGAlgebraMapImpl(source, target, gLinearMap)
        }

        public operator fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: DGAlgebra<D, BS, S, V, M>,
            target: DGAlgebra<D, BT, S, V, M>,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getLinearMap: (D) -> LinearMap<BS, BT, S, V, M>
        ): DGAlgebraMap<D, BS, BT, S, V, M> {
            val gLinearMap = GAlgebraMap(source, target, matrixSpace, name, getLinearMap)
            return DGAlgebraMap(source, target, gLinearMap)
        }

        public fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: DGAlgebra<D, BS, S, V, M>,
            target: DGAlgebra<D, BT, S, V, M>,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, BT, S, V>>
        ): DGAlgebraMap<D, BS, BT, S, V, M> {
            val gLinearMap = GAlgebraMap.fromGVectors(source, target, matrixSpace, name, getGVectors)
            return DGAlgebraMap(source, target, gLinearMap)
        }
    }
}

internal class DGAlgebraMapImpl<D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val source: DGAlgebra<D, BS, S, V, M>,
    override val target: DGAlgebra<D, BT, S, V, M>,
    gLinearMap: GAlgebraMap<D, BS, BT, S, V, M>,
) : DGAlgebraMap<D, BS, BT, S, V, M>,
    DGLinearMap<D, BS, BT, S, V, M> by DGLinearMapImpl(source, target, gLinearMap)

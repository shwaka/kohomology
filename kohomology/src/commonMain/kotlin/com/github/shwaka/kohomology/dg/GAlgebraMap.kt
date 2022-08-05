package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap

public interface GAlgebraMap<D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GLinearMap<D, BS, BT, S, V, M> {
    override val source: GAlgebra<D, BS, S, V, M>
    override val target: GAlgebra<D, BT, S, V, M>

    public operator fun <BR : BasisName> times(other: GAlgebraMap<D, BR, BS, S, V, M>): GAlgebraMap<D, BR, BT, S, V, M> {
        require(other.target == this.source) {
            "Cannot composite graded linear maps since the source of $this and the target of $other are different"
        }
        return GAlgebraMap(
            source = other.source,
            target = this.target,
            matrixSpace = this.matrixSpace,
            name = "${this.name} + ${other.name}",
        ) { degree -> this[degree] * other[degree] }
    }

    public companion object {
        public operator fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: GAlgebra<D, BS, S, V, M>,
            target: GAlgebra<D, BT, S, V, M>,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getLinearMap: (D) -> LinearMap<BS, BT, S, V, M>
        ): GAlgebraMap<D, BS, BT, S, V, M> {
            return GAlgebraMapImpl(source, target, matrixSpace, name, getLinearMap)
        }

        public fun <D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GAlgebra<D, BS, S, V, M>,
            target: GAlgebra<D, BT, S, V, M>,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, BT, S, V>>
        ): GAlgebraMap<D, BS, BT, S, V, M> {
            val getLinearMap = GLinearMap.createGetLinearMap(source, target, 0, matrixSpace, getGVectors)
            return GAlgebraMapImpl(source, target, matrixSpace, name, getLinearMap)
        }
    }
}

// override types of source and target
public class GAlgebraMapImpl<D : Degree, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val source: GAlgebra<D, BS, S, V, M>,
    override val target: GAlgebra<D, BT, S, V, M>,
    matrixSpace: MatrixSpace<S, V, M>,
    name: String,
    getLinearMap: (D) -> LinearMap<BS, BT, S, V, M>
) : GAlgebraMap<D, BS, BT, S, V, M>,
    GLinearMap<D, BS, BT, S, V, M> by GLinearMap(source, target, 0, matrixSpace, name, getLinearMap)

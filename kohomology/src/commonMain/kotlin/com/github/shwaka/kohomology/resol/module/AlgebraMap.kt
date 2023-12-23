package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public interface AlgebraMap<
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > {

    public val source: Algebra<BS, S, V, M>
    public val target: Algebra<BT, S, V, M>
    public val underlyingLinearMap: LinearMap<BS, BT, S, V, M>

    public val matrixSpace: MatrixSpace<S, V, M>
        get() = source.matrixSpace

    public operator fun invoke(vector: Vector<BS, S, V>): Vector<BT, S, V> {
        return this.underlyingLinearMap(vector)
    }

    public companion object {
        public operator fun <
            BS : BasisName,
            BT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            source: Algebra<BS, S, V, M>,
            target: Algebra<BT, S, V, M>,
            underlyingLinearMap: LinearMap<BS, BT, S, V, M>,
        ): AlgebraMap<BS, BT, S, V, M> {
            return AlgebraMapImpl(source, target, underlyingLinearMap)
        }
    }
}

private class AlgebraMapImpl<
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >
(
    override val source: Algebra<BS, S, V, M>,
    override val target: Algebra<BT, S, V, M>,
    override val underlyingLinearMap: LinearMap<BS, BT, S, V, M>,
) : AlgebraMap<BS, BT, S, V, M>

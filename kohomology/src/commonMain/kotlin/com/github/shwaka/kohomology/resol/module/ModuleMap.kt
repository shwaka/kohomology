package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public class ModuleMap<
    BA : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >
(
    public val source: Module<BA, BS, S, V, M>,
    public val target: Module<BA, BT, S, V, M>,
    public val matrix: M,
) {
    public val underlyingLinearMap: LinearMap<BS, BT, S, V, M> by lazy {
        LinearMap.fromMatrix(
            matrixSpace = source.matrixSpace,
            source = source.underlyingVectorSpace,
            target = target.underlyingVectorSpace,
            matrix = matrix,
        )
    }

    public operator fun invoke(vector: Vector<BS, S, V>): Vector<BT, S, V> {
        return this.underlyingLinearMap(vector)
    }
}

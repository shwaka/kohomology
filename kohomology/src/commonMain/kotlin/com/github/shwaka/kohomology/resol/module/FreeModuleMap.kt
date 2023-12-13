package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public interface FreeModuleMap<
    BA : BasisName,
    BVS : BasisName,
    BVT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : ModuleMap<BA, FreeModuleBasisName<BA, BVS>, FreeModuleBasisName<BA, BVT>, S, V, M> {

    override val source: FreeModule<BA, BVS, S, V, M>
    override val target: FreeModule<BA, BVT, S, V, M>
    public val matrixSpace: MatrixSpace<S, V, M>
        get() = source.matrixSpace
    public val inducedMapWithoutCoeff: LinearMap<BVS, BVT, S, V, M>

    public companion object {
        public fun <BA : BasisName, BVS : BasisName, BVT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
            fromValuesOnGeneratingBasis(
            source: FreeModule<BA, BVS, S, V, M>,
            target: FreeModule<BA, BVT, S, V, M>,
            values: List<Vector<FreeModuleBasisName<BA, BVT>, S, V>>
        ): FreeModuleMap<BA, BVS, BVT, S, V, M> {
            val moduleMapFromFreeModule = ModuleMapFromFreeModule.fromValuesOnGeneratingBasis(source, target, values)
            val underlyingLinearMap = moduleMapFromFreeModule.underlyingLinearMap
            return FreeModuleMapImpl(source, target, underlyingLinearMap)
        }
    }
}

private class FreeModuleMapImpl<
    BA : BasisName,
    BVS : BasisName,
    BVT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >
(
    override val source: FreeModule<BA, BVS, S, V, M>,
    override val target: FreeModule<BA, BVT, S, V, M>,
    override val underlyingLinearMap: LinearMap<FreeModuleBasisName<BA, BVS>, FreeModuleBasisName<BA, BVT>, S, V, M>,
) : FreeModuleMap<BA, BVS, BVT, S, V, M> {
    override val inducedMapWithoutCoeff: LinearMap<BVS, BVT, S, V, M> by lazy {
        val proj = this.target.projection
        val vectors = this.source.getGeneratingBasis().map { vector ->
            proj(this(vector))
        }
        LinearMap.fromVectors(
            source = this.source.vectorSpaceWithoutCoeff,
            target = this.target.vectorSpaceWithoutCoeff,
            matrixSpace = this.matrixSpace,
            vectors = vectors,
        )
    }
}

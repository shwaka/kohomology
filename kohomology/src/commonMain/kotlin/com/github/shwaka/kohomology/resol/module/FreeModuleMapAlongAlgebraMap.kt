package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.AlgebraMap
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public interface FreeModuleMapAlongAlgebraMap<
    BAS : BasisName,
    BAT : BasisName,
    BVS : BasisName,
    BVT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : ModuleMapAlongAlgebraMapFromFreeModule<BAS, BAT, BVS, FreeModuleBasisName<BAT, BVT>, S, V, M> {

    override val target: FreeModule<BAT, BVT, S, V, M>
    public val matrixSpace: MatrixSpace<S, V, M>
        get() = source.matrixSpace
    public val tensorWithBaseField: LinearMap<BVS, BVT, S, V, M>

    public operator fun <BVS0 : BasisName> times(
        other: FreeModuleMap<BAS, BVS0, BVS, S, V, M>
    ): FreeModuleMapAlongAlgebraMap<BAS, BAT, BVS0, BVT, S, V, M> {
        require(other.target == this.source) {
            "Cannot compose $this and $other: the source of $this and the target of $other are different"
        }
        val values = other.source.getGeneratingBasis().map { vector ->
            this(other(vector))
        }
        return FreeModuleMapAlongAlgebraMap.fromValuesOnGeneratingBasis(
            source = other.source,
            target = this.target,
            algebraMap = this.algebraMap,
            values = values,
        )
    }

    public companion object {
        public fun <BAS : BasisName, BAT : BasisName, BVS : BasisName, BVT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
        fromValuesOnGeneratingBasis(
            source: FreeModule<BAS, BVS, S, V, M>,
            target: FreeModule<BAT, BVT, S, V, M>,
            algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
            values: List<Vector<FreeModuleBasisName<BAT, BVT>, S, V>>
        ): FreeModuleMapAlongAlgebraMap<BAS, BAT, BVS, BVT, S, V, M> {
            val restrictedTarget = RestrictedModule(target, algebraMap)
            val moduleMap = ModuleMapFromFreeModule.fromValuesOnGeneratingBasis(source, restrictedTarget, values)
            return FreeModuleMapAlongAlgebraMapImpl(source, target, algebraMap, moduleMap)
        }
    }
}

private class FreeModuleMapAlongAlgebraMapImpl<
    BAS : BasisName,
    BAT : BasisName,
    BVS : BasisName,
    BVT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val source: FreeModule<BAS, BVS, S, V, M>,
    override val target: FreeModule<BAT, BVT, S, V, M>,
    override val algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
    override val moduleMap: ModuleMap<BAS, FreeModuleBasisName<BAS, BVS>, FreeModuleBasisName<BAT, BVT>, S, V, M>
) : FreeModuleMapAlongAlgebraMap<BAS, BAT, BVS, BVT, S, V, M> {
    override val underlyingLinearMap: LinearMap<FreeModuleBasisName<BAS, BVS>, FreeModuleBasisName<BAT, BVT>, S, V, M>
        get() = moduleMap.underlyingLinearMap

    override val tensorWithBaseField: LinearMap<BVS, BVT, S, V, M> by lazy {
        val proj = this.target.projection
        val vectors = this.source.getGeneratingBasis().map { vector ->
            proj(this(vector))
        }
        LinearMap.fromVectors(
            source = this.source.tensorWithBaseField,
            target = this.target.tensorWithBaseField,
            matrixSpace = this.matrixSpace,
            vectors = vectors,
        )
    }
}

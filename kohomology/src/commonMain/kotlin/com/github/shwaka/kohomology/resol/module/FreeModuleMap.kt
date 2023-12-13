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
            require(source.coeffAlgebra == target.coeffAlgebra) {
                "cannot consider FreeModuleMap between different coefficient algebras: " +
                    "${source.coeffAlgebra} and ${target.coeffAlgebra}"
            }
            require(values.size == source.generatingBasisNames.size) {
                "values should have the same size with source.generatingBasisNames; " +
                    "values.size=${values.size}, " +
                    "source.generatingBasisNames.size=${source.generatingBasisNames.size}"
            }
            val coeffAlgebra = source.coeffAlgebra
            val vectors = source.underlyingVectorSpace.basisNames.map { freeModuleBasisName ->
                val coeff = coeffAlgebra.fromBasisName(freeModuleBasisName.algebraBasisName)
                val index: Int = source.generatingBasisNames.indexOf(freeModuleBasisName.generatingBasisName)
                target.context.run {
                    coeff * values[index]
                }
            }
            val underlyingLinearMap = LinearMap.fromVectors(
                source = source.underlyingVectorSpace,
                target = target.underlyingVectorSpace,
                matrixSpace = source.matrixSpace,
                vectors = vectors,
            )
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

package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public class FreeModuleMap<
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
    underlyingLinearMap: LinearMap<FreeModuleBasis<BA, BVS>, FreeModuleBasis<BA, BVT>, S, V, M>,
) : ModuleMap<BA, FreeModuleBasis<BA, BVS>, FreeModuleBasis<BA, BVT>, S, V, M>(
    source, target, underlyingLinearMap
) {
    public companion object {
        public fun <BA : BasisName, BVS : BasisName, BVT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
        fromValuesOnGeneratingBasis(
            source: FreeModule<BA, BVS, S, V, M>,
            target: FreeModule<BA, BVT, S, V, M>,
            values: List<Vector<FreeModuleBasis<BA, BVT>, S, V>>
        ): FreeModuleMap<BA, BVS, BVT, S, V, M> {
            require(source.coeffAlgebra == target.coeffAlgebra) {
                "cannot consider FreeModuleMap between different coefficient algebras: " +
                    "${source.coeffAlgebra} and ${target.coeffAlgebra}"
            }
            val coeffAlgebra = source.coeffAlgebra
            val vectors = source.underlyingVectorSpace.basisNames.map { freeModuleBasis ->
                val coeff = coeffAlgebra.fromBasisName(freeModuleBasis.algebraBasisName)
                val index: Int = source.generatingBasisNames.indexOf(freeModuleBasis.generatingBasisName)
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
            return FreeModuleMap(source, target, underlyingLinearMap)
        }
    }
}

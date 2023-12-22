package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public interface ModuleMapFromFreeModule<
    BA : BasisName,
    BVS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : ModuleMap<BA, FreeModuleBasisName<BA, BVS>, BT, S, V, M> {

    override val source: FreeModule<BA, BVS, S, V, M>

    public fun <B : BasisName> liftAlong(
        moduleMap: ModuleMap<BA, B, BT, S, V, M>
    ): ModuleMapFromFreeModule<BA, BVS, B, S, V, M> {
        // Find lift of the diagram:
        //
        //           moduleMap.source
        //               |
        //               | moduleMap
        //               |
        //               v
        // source ---> target
        require(moduleMap.target == this.target) {
            "The target modules of module maps $this and $moduleMap must be same"
        }
        val values: List<Vector<B, S, V>> = this.source.getGeneratingBasis().map { vector ->
            moduleMap.underlyingLinearMap.findPreimage(this(vector))
                ?: throw IllegalArgumentException(
                    "$vector is not contained in the image of the module map $moduleMap"
                )
        }
        return ModuleMapFromFreeModule.fromValuesOnGeneratingBasis(
            source = this.source,
            target = moduleMap.source,
            values = values,
        )
    }

    public companion object {
        public fun <BA : BasisName, BVS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>
        fromValuesOnGeneratingBasis(
            source: FreeModule<BA, BVS, S, V, M>,
            target: Module<BA, BT, S, V, M>,
            values: List<Vector<BT, S, V>>,
        ): ModuleMapFromFreeModule<BA, BVS, BT, S, V, M> {
            require(source.coeffAlgebra == target.coeffAlgebra) {
                "cannot consider ModuleMapFromFree between different coefficient algebras: " +
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
            return ModuleMapFromFreeModuleImpl(source, target, underlyingLinearMap)
        }
    }
}

private class ModuleMapFromFreeModuleImpl<
    BA : BasisName,
    BVS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val source: FreeModule<BA, BVS, S, V, M>,
    override val target: Module<BA, BT, S, V, M>,
    override val underlyingLinearMap: LinearMap<FreeModuleBasisName<BA, BVS>, BT, S, V, M>,
) : ModuleMapFromFreeModule<BA, BVS, BT, S, V, M>

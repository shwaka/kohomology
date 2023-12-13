package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface RestrictedModule<BAS : BasisName, BAT : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    Module<BAS, B, S, V, M> {

    public val originalModule: Module<BAT, B, S, V, M>
    public val algebraMap: AlgebraMap<BAS, BAT, S, V, M>

    public companion object {
        public operator fun <BAS : BasisName, BAT : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            originalModule: Module<BAT, B, S, V, M>,
            algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
        ): RestrictedModule<BAS, BAT, B, S, V, M> {
            return RestrictedModuleImpl(originalModule, algebraMap)
        }
    }
}

private class RestrictedModuleImpl<BAS : BasisName, BAT : BasisName, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val originalModule: Module<BAT, B, S, V, M>,
    override val algebraMap: AlgebraMap<BAS, BAT, S, V, M>,
) : RestrictedModule<BAS, BAT, B, S, V, M> {
    override val underlyingVectorSpace: VectorSpace<B, S, V> = originalModule.underlyingVectorSpace
    override val coeffAlgebra: Algebra<BAS, S, V, M> = algebraMap.source
    override val context: ModuleContext<BAS, B, S, V, M> = ModuleContextImpl(this)
    override val matrixSpace: MatrixSpace<S, V, M> = originalModule.matrixSpace
    override val action: BilinearMap<BAS, B, B, S, V, M> by lazy {
        ValueBilinearMap(
            source1 = this.coeffAlgebra,
            source2 = this.underlyingVectorSpace,
            target = this.underlyingVectorSpace,
            matrixSpace = this.matrixSpace,
        ) { algebraBasisName, moduleBasisName ->
            val algebraElement = this.coeffAlgebra.fromBasisName(algebraBasisName)
            val moduleElement = this.underlyingVectorSpace.fromBasisName(moduleBasisName)
            this.originalModule.act(
                this.algebraMap(algebraElement),
                moduleElement,
            )
        }
    }
}

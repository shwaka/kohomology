package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.algebra.MonoidRing
import com.github.shwaka.kohomology.resol.algebra.MonoidRingMap
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface RestrictedTrivialModule<
    BES : FiniteMonoidElement,
    BET : FiniteMonoidElement,
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : TrivialModule<BES, B, S, V, M>, RestrictedModule<BES, BET, B, S, V, M> {

    override val originalModule: TrivialModule<BET, B, S, V, M>
    override val algebraMap: MonoidRingMap<BES, BET, S, V, M>

    public companion object {
        public operator fun <
            BES : FiniteMonoidElement,
            BET : FiniteMonoidElement,
            B : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            originalModule: TrivialModule<BET, B, S, V, M>,
            algebraMap: MonoidRingMap<BES, BET, S, V, M>,
        ): RestrictedTrivialModule<BES, BET, B, S, V, M> {
            return RestrictedTrivialModuleImpl(originalModule, algebraMap)
        }
    }
}

private class RestrictedTrivialModuleImpl<
    BES : FiniteMonoidElement,
    BET : FiniteMonoidElement,
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val originalModule: TrivialModule<BET, B, S, V, M>,
    override val algebraMap: MonoidRingMap<BES, BET, S, V, M>,
) : RestrictedTrivialModule<BES, BET, B, S, V, M> {
    override val underlyingVectorSpace: VectorSpace<B, S, V> = originalModule.underlyingVectorSpace
    override val coeffAlgebra: MonoidRing<BES, S, V, M> = algebraMap.source
    override val context: ModuleContext<BES, B, S, V, M> = ModuleContext(this)
    override val matrixSpace: MatrixSpace<S, V, M> = originalModule.matrixSpace
    override val action: BilinearMap<BES, B, B, S, V, M> by lazy {
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

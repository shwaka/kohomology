package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.resol.monoid.FiniteMonoidElement
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.BilinearMap
import com.github.shwaka.kohomology.vectsp.StringBasisName
import com.github.shwaka.kohomology.vectsp.ValueBilinearMap
import com.github.shwaka.kohomology.vectsp.VectorSpace

public interface TrivialModule<
    E : FiniteMonoidElement,
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : Module<E, B, S, V, M> {
    override val coeffAlgebra: MonoidRing<E, S, V, M>

    public companion object {
        public operator fun <
            E : FiniteMonoidElement,
            B : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            underlyingVectorSpace: VectorSpace<B, S, V>,
            coeffAlgebra: MonoidRing<E, S, V, M>,
        ): TrivialModule<E, B, S, V, M> {
            return TrivialModuleImpl(underlyingVectorSpace, coeffAlgebra)
        }

        public fun <
            E : FiniteMonoidElement,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > baseField(
            coeffAlgebra: MonoidRing<E, S, V, M>,
        ): TrivialModule<E, StringBasisName, S, V, M> {
            val vectorSpace = VectorSpace(coeffAlgebra.numVectorSpace, listOf("x"))
            return TrivialModule(vectorSpace, coeffAlgebra)
        }
    }
}

private class TrivialModuleImpl<
    E : FiniteMonoidElement,
    B : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val underlyingVectorSpace: VectorSpace<B, S, V>,
    override val coeffAlgebra: MonoidRing<E, S, V, M>,
) : TrivialModule<E, B, S, V, M> {
    override val context: ModuleContext<E, B, S, V, M> = ModuleContextImpl(this)
    override val matrixSpace: MatrixSpace<S, V, M> = coeffAlgebra.matrixSpace

    override val action: BilinearMap<E, B, B, S, V, M> by lazy {
        ValueBilinearMap(
            source1 = this.coeffAlgebra,
            source2 = this.underlyingVectorSpace,
            target = this.underlyingVectorSpace,
            matrixSpace = this.matrixSpace,
            values = List(this.coeffAlgebra.dim) { this.underlyingVectorSpace.getBasis() },
        )
    }
}

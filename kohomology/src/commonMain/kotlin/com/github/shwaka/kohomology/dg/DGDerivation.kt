package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap

public interface DGDerivation<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    DGLinearMap<D, B, B, S, V, M>, Derivation<D, B, S, V, M> {
    override val source: DGAlgebra<D, B, S, V, M>
    override val target: DGAlgebra<D, B, S, V, M>

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: DGAlgebra<D, B, S, V, M>,
            gLinearMap: Derivation<D, B, S, V, M>,
        ): DGDerivation<D, B, S, V, M> {
            return DGDerivationImpl(source, gLinearMap)
        }

        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: DGAlgebra<D, B, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getLinearMap: (D) -> LinearMap<B, B, S, V, M>
        ): DGDerivation<D, B, S, V, M> {
            val gLinearMap = Derivation(source, degree, matrixSpace, name, getLinearMap)
            return DGDerivationImpl(source, gLinearMap)
        }

        public fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: DGAlgebra<D, B, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, B, S, V>>
        ): DGDerivation<D, B, S, V, M> {
            val gLinearMap = Derivation.fromGVectors(source, degree, matrixSpace, name, getGVectors)
            return DGDerivationImpl(source, gLinearMap)
        }
    }
}

private class DGDerivationImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val source: DGAlgebra<D, B, S, V, M>,
    gLinearMap: Derivation<D, B, S, V, M>,
) : DGDerivation<D, B, S, V, M>,
    DGLinearMap<D, B, B, S, V, M> by DGLinearMap(source, source, gLinearMap) {
    override val target: DGAlgebra<D, B, S, V, M> = source
}

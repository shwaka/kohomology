package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap

public interface MagmaDerivation<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GLinearMap<D, B, B, S, V, M> {
    override val source: GMagma<D, B, S, V, M>
    override val target: GMagma<D, B, S, V, M>

    public companion object {
        public fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GMagma<D, B, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, B, S, V>>
        ): MagmaDerivation<D, B, S, V, M> {
            val getLinearMap = GLinearMap.createGetLinearMap(source, source, degree, matrixSpace, getGVectors)
            return MagmaDerivationImpl(source, degree, matrixSpace, name, getLinearMap)
        }
    }
}

private class MagmaDerivationImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val source: GMagma<D, B, S, V, M>,
    degree: D,
    matrixSpace: MatrixSpace<S, V, M>,
    name: String,
    getLinearMap: (D) -> LinearMap<B, B, S, V, M>
) : MagmaDerivation<D, B, S, V, M>,
    GLinearMap<D, B, B, S, V, M> by GLinearMap(source, source, degree, matrixSpace, name, getLinearMap) {
    override val target: GMagma<D, B, S, V, M> = source
}

public interface Derivation<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    MagmaDerivation<D, B, S, V, M> {
    override val source: GAlgebra<D, B, S, V, M>
    override val target: GAlgebra<D, B, S, V, M>

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            source: GAlgebra<D, B, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getLinearMap: (D) -> LinearMap<B, B, S, V, M>
        ): Derivation<D, B, S, V, M> {
            return DerivationImpl(source, degree, matrixSpace, name, getLinearMap)
        }

        public fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GAlgebra<D, B, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, B, S, V>>
        ): Derivation<D, B, S, V, M> {
            val getLinearMap = GLinearMap.createGetLinearMap(source, source, degree, matrixSpace, getGVectors)
            return DerivationImpl(source, degree, matrixSpace, name, getLinearMap)
        }
    }
}

private class DerivationImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val source: GAlgebra<D, B, S, V, M>,
    degree: D,
    matrixSpace: MatrixSpace<S, V, M>,
    name: String,
    getLinearMap: (D) -> LinearMap<B, B, S, V, M>
) : Derivation<D, B, S, V, M>,
    MagmaDerivation<D, B, S, V, M> by MagmaDerivationImpl(source, degree, matrixSpace, name, getLinearMap) {
    override val target: GAlgebra<D, B, S, V, M> = source
}

public interface LieDerivation<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    MagmaDerivation<D, B, S, V, M> {
    override val source: GLieAlgebra<D, B, S, V, M>
    override val target: GLieAlgebra<D, B, S, V, M>

    public companion object {
        public fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GLieAlgebra<D, B, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, B, S, V>>
        ): LieDerivation<D, B, S, V, M> {
            val getLinearMap = GLinearMap.createGetLinearMap(source, source, degree, matrixSpace, getGVectors)
            return LieDerivationImpl(source, degree, matrixSpace, name, getLinearMap)
        }
    }
}

private class LieDerivationImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val source: GLieAlgebra<D, B, S, V, M>,
    degree: D,
    matrixSpace: MatrixSpace<S, V, M>,
    name: String,
    getLinearMap: (D) -> LinearMap<B, B, S, V, M>
) : LieDerivation<D, B, S, V, M>,
    MagmaDerivation<D, B, S, V, M> by MagmaDerivationImpl(source, degree, matrixSpace, name, getLinearMap) {
    override val target: GLieAlgebra<D, B, S, V, M> = source
}

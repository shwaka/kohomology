package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap

public open class MagmaDerivation<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    source: GMagma<D, B, S, V, M>,
    degree: D,
    matrixSpace: MatrixSpace<S, V, M>,
    name: String,
    getLinearMap: (D) -> LinearMap<B, B, S, V, M>
) : GLinearMap<D, B, B, S, V, M>(source, source, degree, matrixSpace, name, getLinearMap) {
    public companion object {
        public fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GMagma<D, B, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, B, S, V>>
        ): MagmaDerivation<D, B, S, V, M> {
            val getLinearMap = GLinearMap.createGetLinearMap(source, source, degree, matrixSpace, getGVectors)
            return MagmaDerivation(source, degree, matrixSpace, name, getLinearMap)
        }
    }
}

public class Derivation<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    source: GAlgebra<D, B, S, V, M>,
    degree: D,
    matrixSpace: MatrixSpace<S, V, M>,
    name: String,
    getLinearMap: (D) -> LinearMap<B, B, S, V, M>
) : MagmaDerivation<D, B, S, V, M>(source, degree, matrixSpace, name, getLinearMap) {
    public companion object {
        public fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GAlgebra<D, B, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, B, S, V>>
        ): Derivation<D, B, S, V, M> {
            val getLinearMap = GLinearMap.createGetLinearMap(source, source, degree, matrixSpace, getGVectors)
            return Derivation(source, degree, matrixSpace, name, getLinearMap)
        }
    }
}

public class LieDerivation<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    source: GLieAlgebra<D, B, S, V, M>,
    degree: D,
    matrixSpace: MatrixSpace<S, V, M>,
    name: String,
    getLinearMap: (D) -> LinearMap<B, B, S, V, M>
) : MagmaDerivation<D, B, S, V, M>(source, degree, matrixSpace, name, getLinearMap) {
    public companion object {
        public fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectors(
            source: GLieAlgebra<D, B, S, V, M>,
            degree: D,
            matrixSpace: MatrixSpace<S, V, M>,
            name: String,
            getGVectors: (D) -> List<GVector<D, B, S, V>>
        ): LieDerivation<D, B, S, V, M> {
            val getLinearMap = GLinearMap.createGetLinearMap(source, source, degree, matrixSpace, getGVectors)
            return LieDerivation(source, degree, matrixSpace, name, getLinearMap)
        }
    }
}

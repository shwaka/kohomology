package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public open class ModuleMap<
    BA : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >
(
    public open val source: Module<BA, BS, S, V, M>,
    public open val target: Module<BA, BT, S, V, M>,
    public val underlyingLinearMap: LinearMap<BS, BT, S, V, M>,
) {
    public operator fun invoke(vector: Vector<BS, S, V>): Vector<BT, S, V> {
        return this.underlyingLinearMap(vector)
    }

    public companion object {
        public fun <BA : BasisName, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromMatrix(
            source: Module<BA, BS, S, V, M>,
            target: Module<BA, BT, S, V, M>,
            matrix: M,
        ): ModuleMap<BA, BS, BT, S, V, M> {
            require(source.coefficientAlgebra == target.coefficientAlgebra) {
                "cannot consider ModuleMap between different coefficient algebras: " +
                    "${source.coefficientAlgebra} and ${target.coefficientAlgebra}"
            }
            val underlyingLinearMap = LinearMap.fromMatrix(
                matrixSpace = source.matrixSpace,
                source = source.underlyingVectorSpace,
                target = target.underlyingVectorSpace,
                matrix = matrix,
            )
            return ModuleMap(source, target, underlyingLinearMap)
        }

        public fun <BA : BasisName, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromVectors(
            source: Module<BA, BS, S, V, M>,
            target: Module<BA, BT, S, V, M>,
            vectors: List<Vector<BT, S, V>>,
        ): ModuleMap<BA, BS, BT, S, V, M> {
            require(source.coefficientAlgebra == target.coefficientAlgebra) {
                "cannot consider ModuleMap between different coefficient algebras: " +
                    "${source.coefficientAlgebra} and ${target.coefficientAlgebra}"
            }
            val underlyingLinearMap = LinearMap.fromVectors(
                matrixSpace = source.matrixSpace,
                source = source.underlyingVectorSpace,
                target = target.underlyingVectorSpace,
                vectors = vectors,
            )
            return ModuleMap(source, target, underlyingLinearMap)
        }
    }
}

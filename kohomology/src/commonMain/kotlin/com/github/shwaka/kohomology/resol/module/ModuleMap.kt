package com.github.shwaka.kohomology.resol.module

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap
import com.github.shwaka.kohomology.vectsp.Vector

public interface ModuleMap<
    BA : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > {

    public val source: Module<BA, BS, S, V, M>
    public val target: Module<BA, BT, S, V, M>
    public val underlyingLinearMap: LinearMap<BS, BT, S, V, M>

    public operator fun invoke(vector: Vector<BS, S, V>): Vector<BT, S, V> {
        return this.underlyingLinearMap(vector)
    }

    public fun kernel(): SubModule<BA, BS, S, V, M> {
        return SubModule(
            totalModule = this.source,
            underlyingVectorSpace = this.underlyingLinearMap.kernel(),
        )
    }

    public companion object {
        public operator fun <
            BA : BasisName,
            BS : BasisName,
            BT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            source: Module<BA, BS, S, V, M>,
            target: Module<BA, BT, S, V, M>,
            underlyingLinearMap: LinearMap<BS, BT, S, V, M>,
        ): ModuleMap<BA, BS, BT, S, V, M> {
            return ModuleMapImpl(source, target, underlyingLinearMap)
        }

        public fun <BA : BasisName, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromMatrix(
            source: Module<BA, BS, S, V, M>,
            target: Module<BA, BT, S, V, M>,
            matrix: M,
        ): ModuleMap<BA, BS, BT, S, V, M> {
            require(source.coeffAlgebra == target.coeffAlgebra) {
                "cannot consider ModuleMap between different coefficient algebras: " +
                    "${source.coeffAlgebra} and ${target.coeffAlgebra}"
            }
            val underlyingLinearMap = LinearMap.fromMatrix(
                matrixSpace = source.matrixSpace,
                source = source.underlyingVectorSpace,
                target = target.underlyingVectorSpace,
                matrix = matrix,
            )
            return ModuleMapImpl(source, target, underlyingLinearMap)
        }

        public fun <BA : BasisName, BS : BasisName, BT : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromVectors(
            source: Module<BA, BS, S, V, M>,
            target: Module<BA, BT, S, V, M>,
            vectors: List<Vector<BT, S, V>>,
        ): ModuleMap<BA, BS, BT, S, V, M> {
            require(source.coeffAlgebra == target.coeffAlgebra) {
                "cannot consider ModuleMap between different coefficient algebras: " +
                    "${source.coeffAlgebra} and ${target.coeffAlgebra}"
            }
            val underlyingLinearMap = LinearMap.fromVectors(
                matrixSpace = source.matrixSpace,
                source = source.underlyingVectorSpace,
                target = target.underlyingVectorSpace,
                vectors = vectors,
            )
            return ModuleMapImpl(source, target, underlyingLinearMap)
        }
    }
}

private class ModuleMapImpl<
    BA : BasisName,
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >
(
    override val source: Module<BA, BS, S, V, M>,
    override val target: Module<BA, BT, S, V, M>,
    override val underlyingLinearMap: LinearMap<BS, BT, S, V, M>,
) : ModuleMap<BA, BS, BT, S, V, M>

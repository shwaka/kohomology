package com.github.shwaka.kohomology.resol.algebra

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.LinearMap

public interface OpAlgebraMap<
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    > : AlgebraMap<BS, BT, S, V, M> {

    override val source: OpAlgebra<BS, S, V, M>
    override val target: OpAlgebra<BT, S, V, M>
    public val originalAlgebraMap: AlgebraMap<BS, BT, S, V, M>

    public companion object {
        public operator fun <
            BS : BasisName,
            BT : BasisName,
            S : Scalar,
            V : NumVector<S>,
            M : Matrix<S, V>,
            > invoke(
            originalAlgebraMap: AlgebraMap<BS, BT, S, V, M>,
            source: OpAlgebra<BS, S, V, M> = OpAlgebra(originalAlgebraMap.source),
            target: OpAlgebra<BT, S, V, M> = OpAlgebra(originalAlgebraMap.target),
        ): OpAlgebraMap<BS, BT, S, V, M> {
            return OpAlgebraMapImpl(originalAlgebraMap, source, target)
        }
    }
}

private class OpAlgebraMapImpl<
    BS : BasisName,
    BT : BasisName,
    S : Scalar,
    V : NumVector<S>,
    M : Matrix<S, V>,
    >(
    override val originalAlgebraMap: AlgebraMap<BS, BT, S, V, M>,
    override val source: OpAlgebra<BS, S, V, M>,
    override val target: OpAlgebra<BT, S, V, M>,
) : OpAlgebraMap<BS, BT, S, V, M> {
    init {
        require(source.isOppositeOf(originalAlgebraMap.source)) {
            "source must be OpAlgebra(${originalAlgebraMap.source}), but $source was given"
        }
        require(target.isOppositeOf(originalAlgebraMap.target)) {
            "target must be OpAlgebra(${originalAlgebraMap.target}), but $target was given"
        }
    }

    override val underlyingLinearMap: LinearMap<BS, BT, S, V, M> by lazy {
        LinearMap.fromMatrix(
            matrixSpace = this.matrixSpace,
            source = this.source,
            target = this.target,
            matrix = this.originalAlgebraMap.underlyingLinearMap.matrix,
        )
    }

    private val _kernel: Ideal<BS, S, V, M> by lazy {
        Ideal(
            totalAlgebra = this.source,
            underlyingVectorSpace = this.underlyingLinearMap.kernel(),
        )
    }
    override fun kernel(): Ideal<BS, S, V, M> {
        return this._kernel
    }

    private val _section: LinearMap<BT, BS, S, V, M> by lazy {
        LinearMap.fromVectors(
            source = this.target,
            target = this.source,
            matrixSpace = this.matrixSpace,
            vectors = this.target.getBasis().map {
                this.underlyingLinearMap.findPreimage(it) ?: throw Exception("This can't happen!")
            },
        )
    }
    override fun section(): LinearMap<BT, BS, S, V, M> {
        require(this.underlyingLinearMap.isSurjective()) {
            "Cannot construct section of non-surjective map"
        }
        return this._section
    }
}

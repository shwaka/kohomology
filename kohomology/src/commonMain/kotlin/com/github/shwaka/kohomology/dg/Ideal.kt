package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName

public interface Ideal<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    SubGVectorSpace<D, B, S, V, M> {
    override val totalGVectorSpace: GAlgebra<D, B, S, V, M>
    public val generatorList: List<GVector<D, B, S, V>>

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            totalGAlgebra: GAlgebra<D, B, S, V, M>,
            subGVectorSpace: SubGVectorSpace<D, B, S, V, M>,
            generatorList: List<GVector<D, B, S, V>>,
        ): Ideal<D, B, S, V, M> {
            return IdealImpl(
                totalGVectorSpace = totalGAlgebra,
                subGVectorSpace = subGVectorSpace,
                generatorList = generatorList,
            )
        }
    }
}

private class IdealImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val totalGVectorSpace: GAlgebra<D, B, S, V, M>,
    subGVectorSpace: SubGVectorSpace<D, B, S, V, M>,
    override val generatorList: List<GVector<D, B, S, V>>,
) : Ideal<D, B, S, V, M>,
    SubGVectorSpace<D, B, S, V, M> by subGVectorSpace {
    init {
        require(totalGVectorSpace.underlyingGVectorSpace == subGVectorSpace.totalGVectorSpace.underlyingGVectorSpace) {
            "subGVectorSpace.totalGVectorSpace (${subGVectorSpace.totalGVectorSpace})" +
                "must be the same as totalGVectorSpace ($totalGVectorSpace)"
        }
    }

    override val name: String
        get() {
            val generatorsString = generatorList.joinToString(", ")
            return "Ideal($generatorsString)"
        }

    override fun toString(): String {
        return this.name
    }
}

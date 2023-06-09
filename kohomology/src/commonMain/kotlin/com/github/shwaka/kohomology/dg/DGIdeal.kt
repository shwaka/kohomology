package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubVectorSpace

public interface DGIdeal<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    SubDGVectorSpace<D, B, S, V, M>,
    Ideal<D, B, S, V, M>
{
    override val totalGVectorSpace: DGAlgebra<D, B, S, V, M>

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            totalDGAlgebra: DGAlgebra<D, B, S, V, M>,
            subDGVectorSpace: SubDGVectorSpace<D, B, S, V, M>,
            generatorList: List<GVector<D, B, S, V>>,
        ): DGIdeal<D, B, S, V, M> {
            return DGIdealImpl(
                totalGVectorSpace = totalDGAlgebra,
                subDGVectorSpace = subDGVectorSpace,
                generatorList = generatorList,
            )
        }
    }
}

private class DGIdealImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val totalGVectorSpace: DGAlgebra<D, B, S, V, M>,
    subDGVectorSpace: SubDGVectorSpace<D, B, S, V, M>,
    override val generatorList: List<GVector<D, B, S, V>>,
) : DGIdeal<D, B, S, V, M>,
    SubDGVectorSpace<D, B, S, V, M> by subDGVectorSpace {
    init {
        require(totalGVectorSpace.underlyingGVectorSpace == subDGVectorSpace.totalGVectorSpace.underlyingGVectorSpace) {
            "subGVectorSpace.totalGVectorSpace (${subDGVectorSpace.totalGVectorSpace})" +
                "must be the same as totalGVectorSpace ($totalGVectorSpace)"
        }
    }

    // Without overriding `get`, we have the following warning:
    // > Delegated member 'fun get(degree: Int): SubVectorSpace<B, S, V, M>'
    // > hides supertype override: public open fun get(degree: Int): SubVectorSpace<B, S, V, M>
    // > defined in com.github.shwaka.kohomology.dg.SubGVectorSpace.
    // > Please specify proper override explicitly
    override fun get(degree: Int): SubVectorSpace<B, S, V, M> {
        return super<DGIdeal>.get(degree)
    }

    override val name: String
        get() {
            val generatorsString = generatorList.joinToString(", ")
            return "DGIdeal($generatorsString)"
        }

    override fun toString(): String {
        return this.name
    }
}

package com.github.shwaka.kohomology.free

import com.github.shwaka.kohomology.dg.GAlgebraContext
import com.github.shwaka.kohomology.dg.GVector
import com.github.shwaka.kohomology.dg.degree.AugmentedDegreeGroup
import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.dg.degree.IntDegree
import com.github.shwaka.kohomology.dg.degree.IntDegreeGroup
import com.github.shwaka.kohomology.free.monoid.Indeterminate
import com.github.shwaka.kohomology.free.monoid.IndeterminateName
import com.github.shwaka.kohomology.free.monoid.NCFreeGMonoid
import com.github.shwaka.kohomology.free.monoid.NCMonomial
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.util.list.duplicates

public interface NCFreeGAlgebra<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    MonoidGAlgebra<D, NCMonomial<D, I>, NCFreeGMonoid<D, I>, S, V, M> {

    override val degreeGroup: AugmentedDegreeGroup<D>
    public val indeterminateList: List<Indeterminate<D, I>>
    override val underlyingGAlgebra: NCFreeGAlgebra<D, I, S, V, M>

    public val generatorList: List<GVector<D, NCMonomial<D, I>, S, V>>
        get() = this.indeterminateList.map { indeterminate ->
            val ncMonomial = NCMonomial(this.degreeGroup, this.indeterminateList, listOf(indeterminate))
            this.fromBasisName(ncMonomial, indeterminate.degree)
        }

    public companion object {
        public operator fun <D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            degreeGroup: AugmentedDegreeGroup<D>,
            indeterminateList: List<Indeterminate<D, I>>,
            getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<NCMonomial<D, I>, S> = InternalPrintConfig.Companion::default,
        ): NCFreeGAlgebra<D, I, S, V, M> {
            return NCFreeGAlgebraImpl(matrixSpace, degreeGroup, indeterminateList, getInternalPrintConfig)
        }

        public operator fun <I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            indeterminateList: List<Indeterminate<IntDegree, I>>
        ): NCFreeGAlgebra<IntDegree, I, S, V, M> {
            return NCFreeGAlgebra(matrixSpace, IntDegreeGroup, indeterminateList)
        }
    }
}

private class NCFreeGAlgebraImpl<D : Degree, I : IndeterminateName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    matrixSpace: MatrixSpace<S, V, M>,
    override val degreeGroup: AugmentedDegreeGroup<D>,
    override val indeterminateList: List<Indeterminate<D, I>>,
    getInternalPrintConfig: (PrintConfig) -> InternalPrintConfig<NCMonomial<D, I>, S> = InternalPrintConfig.Companion::default,
) : NCFreeGAlgebra<D, I, S, V, M>,
    MonoidGAlgebra<D, NCMonomial<D, I>, NCFreeGMonoid<D, I>, S, V, M> by MonoidGAlgebra(
        matrixSpace,
        degreeGroup,
        NCFreeGMonoid(degreeGroup, indeterminateList),
        NCFreeGAlgebraImpl.getName(indeterminateList),
        getInternalPrintConfig,
    ) {

    init {
        val duplicatedIndeterminateList: List<Indeterminate<D, I>> = this.indeterminateList.duplicates()
        require(duplicatedIndeterminateList.isEmpty()) {
            "indeterminateList contains duplicates: $duplicatedIndeterminateList"
        }
    }
    override val context: GAlgebraContext<D, NCMonomial<D, I>, S, V, M> = GAlgebraContext(this)
    override val underlyingGAlgebra: NCFreeGAlgebra<D, I, S, V, M> = this

    override fun toString(printConfig: PrintConfig): String {
        val indeterminateString = this.indeterminateList.joinToString(", ") { it.toString(printConfig) }
        return "T($indeterminateString)"
    }

    companion object {
        private fun <D : Degree, I : IndeterminateName> getName(indeterminateList: List<Indeterminate<D, I>>): String {
            val indeterminateString = indeterminateList.joinToString(", ") { it.toString() }
            return "T($indeterminateString)"
        }
    }
}

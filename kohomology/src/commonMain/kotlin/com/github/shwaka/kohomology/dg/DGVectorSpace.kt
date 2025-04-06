package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.PrintConfig
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubQuotBasis

public interface DGVectorContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GVectorContext<D, B, S, V> {
    public val dgVectorSpace: DGVectorSpace<D, B, S, V, M>

    // use 'by lazy' to avoid 'leaking this in non-final property'
    public val d: GLinearMap<D, B, B, S, V, M>
        get() = this.dgVectorSpace.differential
    public fun GVector<D, B, S, V>.cohomologyClass(): GVector<D, SubQuotBasis<B, S, V>, S, V> {
        return this@DGVectorContext.dgVectorSpace.cohomologyClassOf(this)
    }
    public fun GVector<D, SubQuotBasis<B, S, V>, S, V>.cocycleRepresentative(): GVector<D, B, S, V> {
        return this@DGVectorContext.dgVectorSpace.cocycleRepresentativeOf(this)
    }
    public fun GVector<D, B, S, V>.boundingCochain(): GVector<D, B, S, V>? {
        return this@DGVectorContext.dgVectorSpace.boundingCochainOf(this)
    }
    public fun cohomologyClassOf(cocycle: GVector<D, B, S, V>): GVector<D, SubQuotBasis<B, S, V>, S, V> {
        return this.dgVectorSpace.cohomologyClassOf(cocycle)
    }
    public fun cocycleRepresentativeOf(cohomologyClass: GVector<D, SubQuotBasis<B, S, V>, S, V>): GVector<D, B, S, V> {
        return this.dgVectorSpace.cocycleRepresentativeOf(cohomologyClass)
    }
    public fun boundingCochainOf(cocycle: GVector<D, B, S, V>): GVector<D, B, S, V>? {
        return this.dgVectorSpace.boundingCochainOf(cocycle)
    }

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            dgVectorSpace: DGVectorSpace<D, B, S, V, M>,
        ): DGVectorContext<D, B, S, V, M> {
            return DGVectorContextImpl(dgVectorSpace)
        }
    }
}

private class DGVectorContextImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val dgVectorSpace: DGVectorSpace<D, B, S, V, M>,
) : DGVectorContext<D, B, S, V, M>,
    GVectorContext<D, B, S, V> by GVectorContext(dgVectorSpace)

public interface DGVectorSpace<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GVectorSpace<D, B, S, V> {
    public override val context: DGVectorContext<D, B, S, V, M>
    public val matrixSpace: MatrixSpace<S, V, M>
    public val differential: GLinearMap<D, B, B, S, V, M>
    public val cohomology: SubQuotGVectorSpace<D, B, S, V, M>
    // public fun cohomologyClassOf(cocycle: GVector<D, B, S, V>): GVector<D, SubQuotBasis<B, S, V>, S, V>
    // public fun cocycleRepresentativeOf(cohomologyClass: GVector<D, SubQuotBasis<B, S, V>, S, V>): GVector<D, B, S, V>
    // public fun boundingCochainOf(cocycle: GVector<D, B, S, V>): GVector<D, B, S, V>?
    public fun cohomologyClassOf(cocycle: GVector<D, B, S, V>): GVector<D, SubQuotBasis<B, S, V>, S, V> {
        return DGVectorSpace.getCohomologyClass(this.cohomology, cocycle)
    }

    public fun cocycleRepresentativeOf(cohomologyClass: GVector<D, SubQuotBasis<B, S, V>, S, V>): GVector<D, B, S, V> {
        val vector = cohomologyClass.vector
        val cohomologyOfTheDegree = this.cohomology[cohomologyClass.degree]
        val cocycle = cohomologyOfTheDegree.section(vector)
        return this.fromVector(cocycle, cohomologyClass.degree)
    }

    public fun boundingCochainOf(cocycle: GVector<D, B, S, V>): GVector<D, B, S, V>? {
        return this.differential.findPreimage(cocycle)
    }

    public val cohomologyName: String
        get() = "H(${this.name})"

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            gVectorSpace: GVectorSpace<D, B, S, V>,
            differential: GLinearMap<D, B, B, S, V, M>,
        ): DGVectorSpace<D, B, S, V, M> {
            val cohomology = DGVectorSpace.getCohomology(gVectorSpace, differential)
            return DGVectorSpaceImpl(gVectorSpace, differential, cohomology)
        }

        public fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> fromGVectorSpace(
            matrixSpace: MatrixSpace<S, V, M>,
            gVectorSpace: GVectorSpace<D, B, S, V>,
        ): DGVectorSpace<D, B, S, V, M> {
            val degreeOne = gVectorSpace.degreeGroup.fromInt(1)
            val differential = GLinearMap.getZero(matrixSpace, gVectorSpace, gVectorSpace, degreeOne)
            return DGVectorSpace(gVectorSpace, differential)
        }

        private fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getCohomology(
            gVectorSpace: GVectorSpace<D, B, S, V>,
            differential: GLinearMap<D, B, B, S, V, M>,
        ): SubQuotGVectorSpace<D, B, S, V, M> {
            return Cohomology(gVectorSpace, differential)
        }

        internal fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> getCohomologyClass(
            cohomology: SubQuotGVectorSpace<D, B, S, V, M>,
            cocycle: GVector<D, B, S, V>,
        ): GVector<D, SubQuotBasis<B, S, V>, S, V> {
            require(cohomology.subspaceContains(cocycle)) {
                "$cocycle is not a cocycle"
            }
            return cohomology.projection(cocycle)
        }
    }
}

internal class DGVectorSpaceImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    gVectorSpace: GVectorSpace<D, B, S, V>,
    override val differential: GLinearMap<D, B, B, S, V, M>,
    override val cohomology: SubQuotGVectorSpace<D, B, S, V, M>,
) : DGVectorSpace<D, B, S, V, M>,
    GVectorSpace<D, B, S, V> by gVectorSpace {

    override val context: DGVectorContext<D, B, S, V, M> = DGVectorContext(this)

    override val matrixSpace: MatrixSpace<S, V, M>
        get() = this.differential.matrixSpace

    override val underlyingGVectorSpace: GVectorSpace<D, B, S, V> = gVectorSpace.underlyingGVectorSpace

    override fun toString(): String {
        val name = this.name
        return "($name, d)"
    }
}

private class Cohomology<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    private val gVectorSpace: GVectorSpace<D, B, S, V>,
    differential: GLinearMap<D, B, B, S, V, M>,
) : SubQuotGVectorSpace<D, B, S, V, M> by SubQuotGVectorSpace(
    differential.matrixSpace,
    totalGVectorSpace = gVectorSpace,
    subspaceGenerator = differential.kernel(),
    quotientGenerator = differential.image(),
    name = "H(${gVectorSpace.name})"
) {
    override fun toString(): String {
        return this.toString(PrintConfig.default)
    }

    override fun toString(printConfig: PrintConfig): String {
        return "H(${this.gVectorSpace.toString(printConfig)})"
    }
}

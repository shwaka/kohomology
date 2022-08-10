package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.util.InternalPrintConfig
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace

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
}

internal class DGVectorContextImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    override val dgVectorSpace: DGVectorSpace<D, B, S, V, M>,
) : DGVectorContext<D, B, S, V, M>,
    GVectorContext<D, B, S, V> by GVectorContextImpl(dgVectorSpace)

public interface DGVectorSpace<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> :
    GVectorSpace<D, B, S, V> {
    public override val context: DGVectorContext<D, B, S, V, M>
    public val matrixSpace: MatrixSpace<S, V, M>
    public val differential: GLinearMap<D, B, B, S, V, M>
    public val cohomology: SubQuotGVectorSpace<D, B, S, V, M>
    public fun cohomologyClassOf(cocycle: GVector<D, B, S, V>): GVector<D, SubQuotBasis<B, S, V>, S, V>
    public fun cocycleRepresentativeOf(cohomologyClass: GVector<D, SubQuotBasis<B, S, V>, S, V>): GVector<D, B, S, V>
    public fun boundingCochainOf(cocycle: GVector<D, B, S, V>): GVector<D, B, S, V>?

    public val cohomologyName: String
        get() = "H(${this.name})"

    public companion object {
        public operator fun <D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            gVectorSpace: GVectorSpace<D, B, S, V>,
            differential: GLinearMap<D, B, B, S, V, M>,
            matrixSpace: MatrixSpace<S, V, M>
        ): DGVectorSpace<D, B, S, V, M> {
            return DGVectorSpaceImpl(gVectorSpace, differential, matrixSpace)
        }
    }
}

private class DGVectorSpaceImpl<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    gVectorSpace: GVectorSpace<D, B, S, V>,
    override val differential: GLinearMap<D, B, B, S, V, M>,
    override val matrixSpace: MatrixSpace<S, V, M>
) : DGVectorSpace<D, B, S, V, M>,
    GVectorSpace<D, B, S, V> by gVectorSpace {
    private val cache: MutableMap<D, SubQuotVectorSpace<B, S, V, M>> = mutableMapOf()

    override val context: DGVectorContext<D, B, S, V, M> by lazy {
        DGVectorContextImpl(this)
    }

    override val cohomology: SubQuotGVectorSpace<D, B, S, V, M> by lazy {
        SubQuotGVectorSpace(
            this.matrixSpace.numVectorSpace,
            this.degreeGroup,
            this.cohomologyName,
            InternalPrintConfig.Companion::default,
            this.listDegreesForAugmentedDegree,
            this::getCohomologyVectorSpace,
        )
    }

    fun getCohomologyVectorSpace(degree: D): SubQuotVectorSpace<B, S, V, M> {
        this.cache[degree]?.let {
            // if cache exists
            return it
        }
        // if cache does not exist
        val kernelBasis = this.differential[degree].kernelBasis()
        val previousDegree = this.degreeGroup.context.run { degree - 1 }
        val imageGenerator = this.differential[previousDegree].imageGenerator()
        val subQuotVectorSpace = SubQuotVectorSpace(
            this.matrixSpace,
            this[degree],
            subspaceGenerator = kernelBasis,
            quotientGenerator = imageGenerator,
        )
        this.cache[degree] = subQuotVectorSpace
        return subQuotVectorSpace
    }

    fun getCohomologyVectorSpace(degree: Int): SubQuotVectorSpace<B, S, V, M> {
        return this.getCohomologyVectorSpace(this.degreeGroup.fromInt(degree))
    }

    override fun cohomologyClassOf(cocycle: GVector<D, B, S, V>): GVector<D, SubQuotBasis<B, S, V>, S, V> {
        val vector = cocycle.vector
        val cohomologyOfTheDegree = this.getCohomologyVectorSpace(cocycle.degree)
        if (!cohomologyOfTheDegree.subspaceContains(vector))
            throw IllegalArgumentException("$cocycle is not a cocycle")
        val cohomologyClass = cohomologyOfTheDegree.projection(vector)
        return this.cohomology.fromVector(cohomologyClass, cocycle.degree)
    }

    override fun cocycleRepresentativeOf(cohomologyClass: GVector<D, SubQuotBasis<B, S, V>, S, V>): GVector<D, B, S, V> {
        val vector = cohomologyClass.vector
        val cohomologyOfTheDegree = this.getCohomologyVectorSpace(cohomologyClass.degree)
        val cocycle = cohomologyOfTheDegree.section(vector)
        return this.fromVector(cocycle, cohomologyClass.degree)
    }

    override fun boundingCochainOf(cocycle: GVector<D, B, S, V>): GVector<D, B, S, V>? {
        return this.differential.findPreimage(cocycle)
    }

    override fun toString(): String {
        val name = this.name
        return "($name, d)"
    }
}

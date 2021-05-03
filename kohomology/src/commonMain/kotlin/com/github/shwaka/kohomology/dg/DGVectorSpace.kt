package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.dg.degree.Degree
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.BasisName
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace
import mu.KotlinLogging

interface DGVectorOperations<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    val differential: GLinearMap<D, B, B, S, V, M>
    val cohomology: GVectorSpace<D, SubQuotBasis<B, S, V>, S, V>
    fun cohomologyClassOf(cocycle: GVector<D, B, S, V>): GVector<D, SubQuotBasis<B, S, V>, S, V>
    fun cocycleRepresentativeOf(cohomologyClass: GVector<D, SubQuotBasis<B, S, V>, S, V>): GVector<D, B, S, V>
}

open class DGVectorContext<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<D, B, S, V>,
    dgVectorOperations: DGVectorOperations<D, B, S, V, M>
) : GVectorContext<D, B, S, V>(scalarOperations, numVectorOperations, gVectorOperations),
    DGVectorOperations<D, B, S, V, M> by dgVectorOperations {
    fun d(gVector: GVector<D, B, S, V>): GVector<D, B, S, V> {
        return this.differential(gVector)
    }
    fun GVector<D, B, S, V>.cohomologyClass(): GVector<D, SubQuotBasis<B, S, V>, S, V> {
        return this@DGVectorContext.cohomologyClassOf(this)
    }
    fun GVector<D, SubQuotBasis<B, S, V>, S, V>.cocycleRepresentative(): GVector<D, B, S, V> {
        return this@DGVectorContext.cocycleRepresentativeOf(this)
    }
}

open class DGVectorSpace<D : Degree, B : BasisName, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val gVectorSpace: GVectorSpace<D, B, S, V>,
    override val differential: GLinearMap<D, B, B, S, V, M>,
    val matrixSpace: MatrixSpace<S, V, M>
) : DGVectorOperations<D, B, S, V, M> {
    private val cache: MutableMap<D, SubQuotVectorSpace<B, S, V, M>> = mutableMapOf()
    private val logger = KotlinLogging.logger {}

    open val context by lazy {
        DGVectorContext(this.gVectorSpace.field, this.gVectorSpace.numVectorSpace, this.gVectorSpace, this)
    }

    protected fun getCohomologyVectorSpace(degree: D): SubQuotVectorSpace<B, S, V, M> {
        this.cache[degree]?.let {
            // if cache exists
            this.logger.debug { "cache found for H^$degree(${this.gVectorSpace})" }
            return it
        }
        // if cache does not exist
        this.logger.debug { "cache not found for H^$degree(${this.gVectorSpace}), compute it" }
        val kernelBasis = this.differential[degree].kernelBasis()
        val previousDegree = this.gVectorSpace.degreeGroup.context.run { degree - 1 }
        val imageGenerator = this.differential[previousDegree].imageGenerator()
        val subQuotVectorSpace = SubQuotVectorSpace(
            this.matrixSpace,
            this.gVectorSpace[degree],
            subspaceGenerator = kernelBasis,
            quotientGenerator = imageGenerator,
        )
        this.cache[degree] = subQuotVectorSpace
        return subQuotVectorSpace
    }
    fun getCohomologyVectorSpace(degree: Int): SubQuotVectorSpace<B, S, V, M> {
        return this.getCohomologyVectorSpace(this.gVectorSpace.degreeGroup.fromInt(degree))
    }

    protected val cohomologyName = "H(${this.gVectorSpace.name})"

    override val cohomology: GVectorSpace<D, SubQuotBasis<B, S, V>, S, V> by lazy {
        GVectorSpace(
            this.matrixSpace.numVectorSpace,
            this.gVectorSpace.degreeGroup,
            this.cohomologyName,
            this::getCohomologyVectorSpace
        )
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
        return this.gVectorSpace.fromVector(cocycle, cohomologyClass.degree)
    }

    override fun toString(): String {
        val name = this.gVectorSpace.name
        return "($name, d)"
    }
}

package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.util.Degree
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace
import mu.KotlinLogging

interface DGVectorOperations<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    val differential: GLinearMap<B, B, S, V, M>
    val cohomology: GVectorSpace<SubQuotBasis<B, S, V>, S, V>
    fun cohomologyClassOf(cocycle: GVector<B, S, V>): GVector<SubQuotBasis<B, S, V>, S, V>
    fun cocycleRepresentativeOf(cohomologyClass: GVector<SubQuotBasis<B, S, V>, S, V>): GVector<B, S, V>
}

open class DGVectorContext<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    scalarOperations: ScalarOperations<S>,
    numVectorOperations: NumVectorOperations<S, V>,
    gVectorOperations: GVectorOperations<B, S, V>,
    dgVectorOperations: DGVectorOperations<B, S, V, M>
) : GVectorContext<B, S, V>(scalarOperations, numVectorOperations, gVectorOperations),
    DGVectorOperations<B, S, V, M> by dgVectorOperations {
    fun d(gVector: GVector<B, S, V>): GVector<B, S, V> {
        return this.differential(gVector)
    }
    fun GVector<B, S, V>.cohomologyClass(): GVector<SubQuotBasis<B, S, V>, S, V> {
        return this@DGVectorContext.cohomologyClassOf(this)
    }
    fun GVector<SubQuotBasis<B, S, V>, S, V>.cocycleRepresentative(): GVector<B, S, V> {
        return this@DGVectorContext.cocycleRepresentativeOf(this)
    }
}

open class DGVectorSpace<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val gVectorSpace: GVectorSpace<B, S, V>,
    override val differential: GLinearMap<B, B, S, V, M>,
    val matrixSpace: MatrixSpace<S, V, M>
) : DGVectorOperations<B, S, V, M> {
    private val cache: MutableMap<Degree, SubQuotVectorSpace<B, S, V, M>> = mutableMapOf()
    private val logger = KotlinLogging.logger {}

    open val context by lazy {
        DGVectorContext(this.gVectorSpace.field, this.gVectorSpace.numVectorSpace, this.gVectorSpace, this)
    }

    protected fun getCohomologyVectorSpace(degree: Degree): SubQuotVectorSpace<B, S, V, M> {
        this.cache[degree]?.let {
            // if cache exists
            this.logger.debug { "cache found for H^$degree(${this.gVectorSpace})"}
            return it
        }
        // if cache does not exist
        this.logger.debug { "cache not found for H^$degree(${this.gVectorSpace}), compute it"}
        val kernelBasis = this.differential[degree].kernelBasis()
        val imageGenerator = this.differential[degree - 1].imageGenerator()
        val subQuotVectorSpace = SubQuotVectorSpace(
            this.matrixSpace,
            this.gVectorSpace[degree],
            subspaceGenerator = kernelBasis,
            quotientGenerator = imageGenerator,
        )
        this.cache[degree] = subQuotVectorSpace
        return subQuotVectorSpace
    }

    protected val cohomologyName = "H(${this.gVectorSpace.name})"

    override val cohomology: GVectorSpace<SubQuotBasis<B, S, V>, S, V> by lazy {
        GVectorSpace(
            this.matrixSpace.numVectorSpace,
            this.cohomologyName,
            this::getCohomologyVectorSpace
        )
    }

    override fun cohomologyClassOf(cocycle: GVector<B, S, V>): GVector<SubQuotBasis<B, S, V>, S, V> {
        val vector = cocycle.vector
        val cohomologyOfTheDegree = this.getCohomologyVectorSpace(cocycle.degree)
        if (!cohomologyOfTheDegree.subspaceContains(vector))
            throw IllegalArgumentException("$cocycle is not a cocycle")
        val cohomologyClass = cohomologyOfTheDegree.projection(vector)
        return this.cohomology.fromVector(cohomologyClass, cocycle.degree)
    }

    override fun cocycleRepresentativeOf(cohomologyClass: GVector<SubQuotBasis<B, S, V>, S, V>): GVector<B, S, V> {
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

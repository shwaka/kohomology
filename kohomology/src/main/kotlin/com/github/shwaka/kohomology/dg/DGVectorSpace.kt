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

interface DGVectorOperations<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    val differential: GLinearMap<B, B, S, V, M>
    val cohomology: GVectorSpace<SubQuotBasis<B, S, V>, S, V>
    fun cohomologyClassOf(gVector: GVector<B, S, V>): GVector<SubQuotBasis<B, S, V>, S, V>
}

class DGVectorContext<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
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
}

open class DGVectorSpace<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val gVectorSpace: GVectorSpace<B, S, V>,
    override val differential: GLinearMap<B, B, S, V, M>,
    val matrixSpace: MatrixSpace<S, V, M>
) : DGVectorOperations<B, S, V, M> {
    private val dgVectorContext by lazy {
        DGVectorContext(this.gVectorSpace.field, this.gVectorSpace.numVectorSpace, this.gVectorSpace, this)
    }
    fun <T> withDGVectorContext(block: DGVectorContext<B, S, V, M>.() -> T): T = this.dgVectorContext.block()

    protected fun getCohomologyVectorSpace(degree: Degree): SubQuotVectorSpace<B, S, V, M> {
        // TODO: cache!!
        val kernelBasis = this.differential.getLinearMap(degree).kernelBasis()
        val imageGenerator = this.differential.getLinearMap(degree - 1).imageGenerator()
        return SubQuotVectorSpace(
            this.matrixSpace,
            this.gVectorSpace[degree],
            subspaceGenerator = kernelBasis,
            quotientGenerator = imageGenerator,
        )
    }

    override val cohomology: GVectorSpace<SubQuotBasis<B, S, V>, S, V> by lazy {
        GVectorSpace(
            this.matrixSpace.numVectorSpace,
            this::getCohomologyVectorSpace
        )
    }

    override fun cohomologyClassOf(gVector: GVector<B, S, V>): GVector<SubQuotBasis<B, S, V>, S, V> {
        val vector = gVector.vector
        val cohomologyOfTheDegree = this.getCohomologyVectorSpace(gVector.degree)
        val cohomologyClass = cohomologyOfTheDegree.projection(vector)
        return this.cohomology.fromVector(cohomologyClass, gVector.degree)
    }
}

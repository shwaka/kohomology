package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.Degree
import com.github.shwaka.kohomology.vectsp.GVector
import com.github.shwaka.kohomology.vectsp.GVectorContext
import com.github.shwaka.kohomology.vectsp.GVectorOperations
import com.github.shwaka.kohomology.vectsp.GVectorSpace
import com.github.shwaka.kohomology.vectsp.SubQuotBasis
import com.github.shwaka.kohomology.vectsp.SubQuotVectorSpace
import com.github.shwaka.kohomology.vectsp.VectorSpace

interface DGVectorOperations<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    val differential: GLinearMap<B, B, S, V, M>
    fun cohomology(): GVectorSpace<SubQuotBasis<B, S, V>, S, V>
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

    override fun cohomology(): GVectorSpace<SubQuotBasis<B, S, V>, S, V> {
        val getVectorSpace: (Degree) -> VectorSpace<SubQuotBasis<B, S, V>, S, V> = { degree ->
            val kernelBasis = this.differential.getLinearMap(degree).kernelBasis()
            val imageGenerator = this.differential.getLinearMap(degree - 1).imageGenerator()
            SubQuotVectorSpace(
                this.matrixSpace,
                this.gVectorSpace[degree],
                subspaceGenerator = kernelBasis,
                quotientGenerator = imageGenerator,
            )
        }
        return GVectorSpace(
            this.matrixSpace.numVectorSpace,
            getVectorSpace
        )
    }
}

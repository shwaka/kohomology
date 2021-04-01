package com.github.shwaka.kohomology.dg

import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.NumVectorOperations
import com.github.shwaka.kohomology.linalg.Scalar
import com.github.shwaka.kohomology.linalg.ScalarOperations
import com.github.shwaka.kohomology.vectsp.GVector
import com.github.shwaka.kohomology.vectsp.GVectorContext
import com.github.shwaka.kohomology.vectsp.GVectorOperations
import com.github.shwaka.kohomology.vectsp.GVectorSpace

interface DGVectorOperations<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> {
    val differential: GLinearMap<B, B, S, V, M>
    fun cohomology(): GVectorSpace<B, S, V>
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
    override val differential: GLinearMap<B, B, S, V, M>
) : DGVectorOperations<B, S, V, M> {
    private val dgVectorContext by lazy {
        DGVectorContext(this.gVectorSpace.numVectorSpace.field, this.gVectorSpace.numVectorSpace, this.gVectorSpace, this)
    }
    fun <T> withDGVectorContext(block: DGVectorContext<B, S, V, M>.() -> T): T = this.dgVectorContext.block()

    override fun cohomology(): GVectorSpace<B, S, V> {
        TODO("not implemented")
    }
}

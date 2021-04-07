package com.github.shwaka.kohomology.vectsp

import com.github.shwaka.kococo.debugOnly
import com.github.shwaka.kohomology.linalg.Matrix
import com.github.shwaka.kohomology.linalg.MatrixSpace
import com.github.shwaka.kohomology.linalg.NumVector
import com.github.shwaka.kohomology.linalg.RowEchelonForm
import com.github.shwaka.kohomology.linalg.Scalar

data class SubQuotBasis<B, S : Scalar, V : NumVector<S>>(
    val vector: Vector<B, S, V>
) {
    override fun toString(): String {
        return "[${this.vector}]"
    }
}

private class SubQuotFactory<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>>(
    val matrixSpace: MatrixSpace<S, V, M>,
    val totalVectorSpace: VectorSpace<B, S, V>,
    val subspaceGenerator: List<Vector<B, S, V>>,
    val quotientGenerator: List<Vector<B, S, V>>,
) {
    val numVectorSpace = matrixSpace.numVectorSpace
    val basisNames: List<SubQuotBasis<B, S, V>>
    val transformationMatrix: M
    val projectionMatrix: M
    val sectionMatrix: M
    val quotientDim: Int
    val subQuotDim: Int

    init {
        // check that generators are in totalVectorSpace
        for (vector in subspaceGenerator + quotientGenerator)
            if (vector !in totalVectorSpace)
                throw IllegalArgumentException("The vector $vector is not contained in the vector space $totalVectorSpace")
        debugOnly {
            // check that quotientGenerator is contained in subspaceGenerator
            val pivots = matrixSpace.context.run {
                val quotientMatrix = matrixSpace.fromVectors(quotientGenerator, totalVectorSpace.dim)
                val subspaceMatrix = matrixSpace.fromVectors(subspaceGenerator, totalVectorSpace.dim)
                val matrixForCheck = listOf(subspaceMatrix, quotientMatrix).join()
                matrixForCheck.rowEchelonForm.pivots
            }
            // 'and' だと短絡評価しないっぽい…？
            if (pivots.isNotEmpty() && (pivots.last() >= subspaceGenerator.size))
                throw IllegalArgumentException("The generator for the quotient is not contained in the subspace")
        }
        val joinedMatrix: M = matrixSpace.context.run {
            val quotientMatrix = matrixSpace.fromVectors(quotientGenerator, totalVectorSpace.dim)
            val subspaceMatrix = matrixSpace.fromVectors(subspaceGenerator, totalVectorSpace.dim)
            val id = matrixSpace.getId(totalVectorSpace.dim)
            listOf(quotientMatrix, subspaceMatrix, id).join()
        }
        val rowEchelonForm: RowEchelonForm<S, V, M> = matrixSpace.context.run {
            joinedMatrix.rowEchelonForm
        }
        this.quotientDim = rowEchelonForm.pivots.filter { it < quotientGenerator.size }.size
        this.subQuotDim = rowEchelonForm.pivots.filter {
            (quotientGenerator.size <= it) && (it < quotientGenerator.size + subspaceGenerator.size)
        }.size

        val basisIndices: List<Int> = rowEchelonForm.pivots.slice(quotientDim until (quotientDim + subQuotDim))
        this.basisNames = basisIndices.map { index -> subspaceGenerator[index - quotientGenerator.size] }
            .map { vector -> SubQuotBasis(vector) }

        this.transformationMatrix = matrixSpace.context.run {
            val size = rowEchelonForm.reducedMatrix.colCount
            val dim = totalVectorSpace.dim
            rowEchelonForm.reducedMatrix.colSlice((size - dim) until size)
        }
        this.projectionMatrix = matrixSpace.context.run {
            this@SubQuotFactory.transformationMatrix.rowSlice(quotientDim until (quotientDim + subQuotDim))
        }
        this.sectionMatrix = matrixSpace.fromVectors(
            basisIndices.map { index ->
                subspaceGenerator[index - quotientGenerator.size]
            },
            this.totalVectorSpace.dim
        )
    }

    fun subspaceContains(vector: Vector<B, S, V>): Boolean {
        return this.matrixSpace.context.run {
            val numVector = this@SubQuotFactory.transformationMatrix * vector.numVector
            val start = this@SubQuotFactory.quotientDim + this@SubQuotFactory.subQuotDim
            val limit = this@SubQuotFactory.totalVectorSpace.dim
            (start until limit).all { numVector[it] == zero }
        }
    }

    companion object {
        fun <B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> MatrixSpace<S, V, M>.fromVectors(
            vectors: List<Vector<B, S, V>>,
            dim: Int? = null
        ): M {
            val numVectorList = vectors.map { it.toNumVector() }
            return this.fromNumVectors(numVectorList, dim)
        }
    }
}

class SubQuotVectorSpace<B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> private constructor(
    private val factory: SubQuotFactory<B, S, V, M>
) : VectorSpace<SubQuotBasis<B, S, V>, S, V>(factory.numVectorSpace, factory.basisNames) {
    val projection: LinearMap<B, SubQuotBasis<B, S, V>, S, V, M> by lazy {
        LinearMap.fromMatrix(
            source = this.factory.totalVectorSpace,
            target = this,
            matrixSpace = this.factory.matrixSpace,
            matrix = this.factory.projectionMatrix,
        )
    }
    val section: LinearMap<SubQuotBasis<B, S, V>, B, S, V, M> by lazy {
        LinearMap.fromMatrix(
            source = this,
            target = this.factory.totalVectorSpace,
            matrixSpace = this.factory.matrixSpace,
            matrix = this.factory.sectionMatrix,
        )
    }

    fun subspaceContains(vector: Vector<B, S, V>): Boolean {
        return this.factory.subspaceContains(vector)
    }

    companion object {
        operator fun <B, S : Scalar, V : NumVector<S>, M : Matrix<S, V>> invoke(
            matrixSpace: MatrixSpace<S, V, M>,
            totalVectorSpace: VectorSpace<B, S, V>,
            subspaceGenerator: List<Vector<B, S, V>>,
            quotientGenerator: List<Vector<B, S, V>>,
        ): SubQuotVectorSpace<B, S, V, M> {
            val factory = SubQuotFactory(
                matrixSpace,
                totalVectorSpace,
                subspaceGenerator = subspaceGenerator,
                quotientGenerator = quotientGenerator
            )
            return SubQuotVectorSpace(factory)
        }
    }
}
